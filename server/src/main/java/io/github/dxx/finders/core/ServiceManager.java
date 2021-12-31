package io.github.dxx.finders.core;

import io.github.dxx.finders.constant.Services;
import io.github.dxx.finders.executor.GlobalExecutor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service management.
 *
 * @author dxx
 */
public class ServiceManager {

    /**
     * Map<namespace, Map<serviceName, Service>>.
     */
    private final Map<String, Map<String, Service>> serviceMap = new ConcurrentHashMap<>();

    private final ServiceStore serviceStore = new ServiceStore();

    private final ServiceUpdater serviceUpdater = new ServiceUpdater();

    private final SyncManager syncManager;

    public ServiceManager(SyncManager syncManager) {
        this.syncManager = syncManager;

        GlobalExecutor.executeServiceUpdate(serviceUpdater);
    }

    public void registerInstance(String namespace, String serviceName, Instance instance) {
        Service service = getService(namespace, serviceName);
        if (service == null) {
            synchronized ((namespace + serviceName).intern()) {
                service = createServiceIfAbsent(namespace, serviceName);
            }
        }

        addInstance(service, Collections.singletonList(instance));

        syncManager.sync(service.getNamespace(), service.getServiceName());
    }

    public void deregisterInstance(String namespace, String serviceName, Instance instance) {
        Service service = getService(namespace, serviceName);
        synchronized ((namespace + serviceName).intern()) {
            removeInstance(service, Collections.singletonList(instance));
        }
        syncManager.sync(service.getNamespace(), service.getServiceName());
    }

    public Instance getInstance(String namespace, String serviceName, String cluster, String ip, int port) {
        Service service = getService(namespace, serviceName);
        if (service == null) {
            return null;
        }
        return service.getInstance(cluster, ip, port, true);
    }

    public Service getService(String namespace, String serviceName) {
        Map<String, Service> clusterMap = serviceMap.get(namespace);
        if (clusterMap == null) {
            return null;
        }
        return clusterMap.get(serviceName);
    }

    public void handleInstanceHeartbeat(String namespace, String serviceName, String cluster, String ip, int port) {
        Service service = getService(namespace, serviceName);
        if (service == null) {
            return;
        }
        Instance instance = service.getInstance(cluster, ip, port, false);
        if (instance == null) {
            return;
        }
        service.handleHeartbeat(cluster, ip, port);
    }

    public void updateInstanceStatus(String namespace, String serviceName, Instance instance) {
        Service service = getService(namespace, serviceName);
        if (service == null) {
            return;
        }
        Instance existentInstance = service.getInstance(instance.getCluster(),
                instance.getIp(), instance.getPort(), true);
        if (existentInstance != null) {
            Instance newInstance = new Instance();
            newInstance.setInstanceId(existentInstance.getInstanceId());
            newInstance.setCluster(existentInstance.getCluster());
            newInstance.setServiceName(existentInstance.getServiceName());
            newInstance.setIp(existentInstance.getIp());
            newInstance.setPort(existentInstance.getPort());
            newInstance.setStatus(instance.getStatus());
            newInstance.setLastBeatTimestamp(System.currentTimeMillis());
            addInstance(service, Collections.singletonList(newInstance));
        }
    }

    public Map<String, Map<String, Service>> getServiceMap() {
        return serviceMap;
    }

    public ServiceStore getServiceStore() {
        return serviceStore;
    }

    public Service createService(String namespace, String serviceName) {
        return createServiceIfAbsent(namespace, serviceName);
    }

    private Service createServiceIfAbsent(String namespace, String serviceName) {
        Service service = getService(namespace, serviceName);
        if (service == null) {
            service = new Service(namespace, serviceName);

            serviceMap.computeIfAbsent(namespace, k -> new ConcurrentSkipListMap<>());
            serviceMap.get(namespace).put(serviceName, service);
        }
        return service;
    }

    public void addInstance(Service service, List<Instance> instances) {
        updateInstance(service, instances, Services.ACTION_ADD);
    }

    private void removeInstance(Service service, List<Instance> instances) {
        updateInstance(service, instances, Services.ACTION_REMOVE);
    }

    public void syncInstance(Service service, List<Instance> instances) {
        updateInstance(service, instances, Services.ACTION_SYNC);
    }

    private void updateInstance(Service service, List<Instance> instances, String action) {
        List<Instance> newInstances = new ArrayList<>();
        String serviceKey = ServiceKey.build(service.getNamespace(), service.getServiceName());
        List<Instance> storeInstances = serviceStore.get(serviceKey);
        if (storeInstances != null && storeInstances.size() > 0) {
            newInstances = storeInstances;
        }
        Map<String, Instance> instanceMap = newInstances.stream()
                .collect(Collectors.toMap(Instance::getInstanceId, Function.identity()));

        if (Services.ACTION_SYNC.equals(action)) {
            if (instances.size() > 0) {
                instances.forEach(instance -> instanceMap.put(instance.getInstanceId(), instance));
            } else {
                instanceMap.clear();
            }
        } else {
            instances.forEach(instance -> {
                instanceMap.remove(instance.getInstanceId());
                if (Services.ACTION_ADD.equals(action)) {
                    instanceMap.put(instance.getInstanceId(), instance);
                }
            });
        }

        List<Instance> instanceList = new ArrayList<>(instanceMap.values());
        serviceStore.put(serviceKey, instanceList);

        serviceUpdater.addTask(service, instanceList);
    }
}
