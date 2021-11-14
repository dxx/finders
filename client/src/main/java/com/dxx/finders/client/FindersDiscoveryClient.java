package com.dxx.finders.client;

import com.dxx.finders.client.constant.Services;
import com.dxx.finders.client.loadbalance.LoadBalancer;
import com.dxx.finders.client.loadbalance.LoadBalancerType;
import com.dxx.finders.client.loadbalance.RandomBalancer;
import com.dxx.finders.client.loadbalance.RoundBalancer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Finders discovery client.
 *
 * @author dxx
 */
public class FindersDiscoveryClient implements FindersClient {

    private String namespace;

    private String serverList;

    private FindersClientProxy proxy;

    public FindersDiscoveryClient(String serverList, String namespace,
                                  LoadBalancerType loadBalancerType) {
        this.namespace = namespace;
        this.serverList = serverList;

        init(serverList, namespace, loadBalancerType);
    }

    private void init(String serverList, String namespace, LoadBalancerType loadBalancerType) {
        if (serverList == null || serverList.equals("")) {
            throw new FindersRuntimeException("serverList must not be null");
        }
        List<String> srvList = Arrays.stream(serverList.split(","))
                .collect(Collectors.toList());
        LoadBalancer loadBalancer;
        switch (loadBalancerType) {
            case ROUND:
                loadBalancer = new RoundBalancer(srvList);
                break;
            case RANDOM:
                loadBalancer = new RandomBalancer(srvList);
                break;
            default:
                throw new FindersRuntimeException("loadBalancerType is incorrect");
        }
        this.proxy = new FindersClientProxy(namespace, loadBalancer);
    }

    @Override
    public List<Instance> getAllInstances(String serviceName) {
        return getAllInstances(serviceName, Services.DEFAULT_CLUSTER);
    }

    @Override
    public List<Instance> getAllInstances(String serviceName, String cluster) {
        return getAllInstances(serviceName, Collections.singletonList(cluster));
    }

    @Override
    public List<Instance> getAllInstances(String serviceName, List<String> clusters) {
        return proxy.getAllInstances(serviceName, clusters);
    }

    @Override
    public List<Instance> getInstances(String serviceName, boolean healthy) {
        return getInstances(serviceName, Services.DEFAULT_CLUSTER, healthy);
    }

    @Override
    public List<Instance> getInstances(String serviceName, String cluster, boolean healthy) {
        return getInstances(serviceName, Collections.singletonList(cluster), healthy);
    }

    @Override
    public List<Instance> getInstances(String serviceName, List<String> clusters, boolean healthy) {
        return proxy.getInstances(serviceName, clusters, healthy);
    }

    @Override
    public Instance getInstance(String serviceName, String ip, int port) {
        return getInstance(serviceName, ip, port, Services.DEFAULT_CLUSTER);
    }

    @Override
    public Instance getInstance(String serviceName, String ip, int port, String cluster) {
        return proxy.getInstance(serviceName, ip, port, cluster);
    }

    @Override
    public void registerInstance(String serviceName, String ip, int port) {
        registerInstance(serviceName, ip, port, Services.DEFAULT_CLUSTER);
    }

    @Override
    public void registerInstance(String serviceName, String ip, int port, String cluster) {
        proxy.registerInstance(serviceName, ip, port, cluster);
    }

    @Override
    public void registerInstance(String serviceName, Instance instance) {
        proxy.registerInstance(serviceName, instance);
    }

    @Override
    public void deregisterInstance(String serviceName, String ip, int port) {
        deregisterInstance(serviceName, ip, port, Services.DEFAULT_CLUSTER);
    }

    @Override
    public void deregisterInstance(String serviceName, String ip, int port, String cluster) {
        proxy.deregisterInstance(serviceName, ip, port, cluster);
    }

}
