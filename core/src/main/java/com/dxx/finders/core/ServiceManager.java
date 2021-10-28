package com.dxx.finders.core;

import com.dxx.finders.constant.Services;
import com.dxx.finders.executor.GlobalExecutor;

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

    public ServiceManager() {
        GlobalExecutor.executeServiceUpdateTask(serviceUpdater);
    }

    public void registerInstance(String namespace, String serviceName, Instance instance) {
        Service service = getService(namespace, serviceName);
        if (service == null) {
            synchronized ((namespace + serviceName).intern()) {
                service = createServiceIfAbsent(namespace, serviceName);
            }
        }

        addInstance(service, Collections.singletonList(instance));
    }

    public void deregisterInstance(String namespace, String serviceName, Instance instance) {
        Service service = getService(namespace, serviceName);
        synchronized ((namespace + serviceName).intern()) {
            removeInstance(service, Collections.singletonList(instance));
        }
    }

    public Instance getInstance(String namespace, String serviceName, String clusterName, String ip, int port) {
        Service service = getService(namespace, serviceName);
        if (service == null) {
            return null;
        }

        List<Instance> instances = service.getInstances(Collections.singletonList(clusterName));
        if (instances.size() > 0) {
            Optional<Instance> optionalInstance = instances.stream().filter(item ->
                    ip.equals(item.getIp()) && port == item.getPort()).findFirst();
            if (optionalInstance.isPresent()) {
                return optionalInstance.get();
            }
        }

        return null;
    }

    public Service getService(String namespace, String serviceName) {
        Map<String, Service> clusterMap = serviceMap.get(namespace);
        if (clusterMap == null) {
            return null;
        }
        return clusterMap.get(serviceName);
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

    private void addInstance(Service service, List<Instance> instances) {
        updateInstance(service, instances, Services.ACTION_ADD);
    }

    private void removeInstance(Service service, List<Instance> instances) {
        updateInstance(service, instances, Services.ACTION_REMOVE);
    }

    private void updateInstance(Service service, List<Instance> instances, String action) {
        List<Instance> newInstances = new ArrayList<>();
        List<Instance> storeInstances = serviceStore.get(service.getNamespace(), service.getServiceName());
        if (storeInstances != null && storeInstances.size() > 0) {
            newInstances = storeInstances;
        }
        Map<String, Instance> instanceMap = newInstances.stream()
                .collect(Collectors.toMap(Instance::getInstanceId, Function.identity()));

        instances.forEach(instance -> {
            instanceMap.remove(instance.getInstanceId());
            if (Services.ACTION_ADD.equals(action)) {
                instanceMap.put(instance.getInstanceId(), instance);
            }
        });

        List<Instance> instanceList = new ArrayList<>(instanceMap.values());
        serviceStore.put(service.getNamespace(), service.getServiceName(), instanceList);

        serviceUpdater.addTask(service, instanceList);
    }
}
