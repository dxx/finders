package com.dxx.finders.core;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents the service instance information.
 *
 * @author dxx
 */
public class Service {

    /**
     * Map<cluster, Instances>.
     */
    private Map<String, Set<Instance>> clusterMap = new HashMap<>();

    private String namespace;

    private String serviceName;

    public Service(String namespace, String serviceName) {
        this.namespace = namespace;
        this.serviceName = serviceName;
    }

    public void updateInstance(List<Instance> instances) {
        Map<String, List<Instance>> newClusterMap = new HashMap<>(clusterMap.size());
        clusterMap.keySet().forEach(key -> newClusterMap.put(key, new ArrayList<>()));

        instances.forEach(instance -> {
            List<Instance> instanceList = newClusterMap.computeIfAbsent(
                    instance.getClusterName(), k -> new ArrayList<>());
            instanceList.add(instance);
        });

        newClusterMap.forEach(this::updateInstance);
    }

    public List<Instance> getInstances() {
        return clusterMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    public List<Instance> getInstances(List<String> clusters) {
        List<Instance> instances = new ArrayList<>();
        for (String cluster : clusters) {
            Set<Instance> instanceSet = clusterMap.get(cluster);
            if (instanceSet == null) {
                continue;
            }
            instances.addAll(instanceSet);
        }
        return instances;
    }

    private void updateInstance(String clusterName, List<Instance> instances) {
        clusterMap.put(clusterName, new HashSet<>(instances));
    }

    public String getNamespace() {
        return namespace;
    }

    public String getServiceName() {
        return serviceName;
    }

}