package com.dxx.finders.core;

import java.util.Collections;
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
        service.updateInstance(Collections.singletonList(instance));
    }

}
