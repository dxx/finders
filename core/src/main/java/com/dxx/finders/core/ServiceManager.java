package com.dxx.finders.core;

import com.dxx.finders.constant.Services;
import com.dxx.finders.executor.GlobalExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

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

    public void registerService(String namespace, String serviceName, Instance instance) {
        Service service = getService(namespace, serviceName);
        if (service == null) {
            synchronized ((namespace + serviceName).intern()) {
                service = createServiceIfAbsent(namespace, serviceName);
            }
        }

        addInstance(service, instance);
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

    private void addInstance(Service service, Instance instance) {
        updateInstance(service, instance, Services.ACTION_ADD);
    }

    private void updateInstance(Service service, Instance instance, String action) {
        List<Instance> instances = new ArrayList<>();
        List<Instance> storeInstances = serviceStore.get(service.getNamespace(), service.getServiceName());
        if (storeInstances != null && storeInstances.size() > 0) {
            instances = storeInstances;
        }
        instances.add(instance);
        serviceStore.put(service.getNamespace(), service.getServiceName(), instances);

        serviceUpdater.addTask(service, instances);
    }
}
