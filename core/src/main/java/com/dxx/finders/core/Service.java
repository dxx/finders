package com.dxx.finders.core;

import com.dxx.finders.constant.Services;
import com.dxx.finders.executor.GlobalExecutor;

import java.util.*;
import java.util.concurrent.TimeUnit;
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
    private final Map<String, Set<Instance>> clusterMap = new HashMap<>();

    private final InstanceHealthCheckTask instanceHealthCheckTask = new InstanceHealthCheckTask(this);

    private final String namespace;

    private final String serviceName;

    public Service(String namespace, String serviceName) {
        this.namespace = namespace;
        this.serviceName = serviceName;

        init();
    }

    public void updateInstance(List<Instance> instances) {
        Map<String, List<Instance>> newClusterMap = new HashMap<>(clusterMap.size());
        clusterMap.keySet().forEach(key -> newClusterMap.put(key, new ArrayList<>()));

        instances.forEach(instance -> {
            List<Instance> instanceList = newClusterMap.computeIfAbsent(
                    instance.getCluster(), k -> new ArrayList<>());
            instanceList.add(instance);
        });

        newClusterMap.forEach(this::updateInstance);
    }

    public List<Instance> getAllInstance() {
        return clusterMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    public List<Instance> getAllInstance(List<String> clusters) {
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

    public List<Instance> getInstances() {
        return getAllInstance().stream()
                .filter(in -> in.getStatus() != InstanceStatus.DISABLE).collect(Collectors.toList());
    }

    public List<Instance> getInstances(List<String> clusters) {
        return getAllInstance(clusters).stream()
                .filter(in -> in.getStatus() != InstanceStatus.DISABLE).collect(Collectors.toList());
    }

    public Instance getInstance(String cluster, String ip, int port, boolean disabled) {
        List<Instance> instances;
        if (disabled) {
            instances = getAllInstance(Collections.singletonList(cluster));;
        } else {
            instances = getInstances(Collections.singletonList(cluster));
        }

        if (instances.size() > 0) {
            Optional<Instance> optionalInstance = instances.stream().filter(item ->
                    ip.equals(item.getIp()) && port == item.getPort()).findFirst();
            if (optionalInstance.isPresent()) {
                return optionalInstance.get();
            }
        }
        return null;
    }

    public void handleHeartbeat(String cluster, String ip, int port) {
        Instance instance = new Instance();
        instance.setCluster(cluster);
        instance.setServiceName(getServiceName());
        instance.setIp(ip);
        instance.setPort(port);
        InstanceHeartbeatHandler heartbeatHandler = new InstanceHeartbeatHandler(this, instance);
        GlobalExecutor.scheduleHeartbeatHandler(heartbeatHandler, 0, TimeUnit.MILLISECONDS);
    }

    private void updateInstance(String clusterName, List<Instance> instances) {
        clusterMap.put(clusterName, new HashSet<>(instances));
    }

    private void init() {
        GlobalExecutor.scheduleHealthCheckTask(instanceHealthCheckTask,
                5000, Services.INSTANCE_HEALTH_CHECK_PERIOD, TimeUnit.MILLISECONDS);
    }

    public String getNamespace() {
        return namespace;
    }

    public String getServiceName() {
        return serviceName;
    }

}
