package com.dxx.finders.client;

import com.dxx.finders.client.constant.Services;
import com.dxx.finders.client.loadbalance.LoadBalancer;
import com.dxx.finders.client.loadbalance.LoadBalancerType;
import com.dxx.finders.client.loadbalance.RandomBalancer;
import com.dxx.finders.client.loadbalance.RoundBalancer;
import com.dxx.finders.client.model.Heartbeat;
import com.dxx.finders.client.model.Instance;
import com.dxx.finders.client.reactor.HeartbeatReactor;

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

    private FindersClientConfig config;

    private FindersClientProxy clientProxy;

    private HeartbeatReactor heartbeatReactor;

    public FindersDiscoveryClient(String namespace,
                                  FindersClientConfig clientConfig,
                                  LoadBalancerType loadBalancerType) {
        init(namespace, clientConfig, loadBalancerType);
    }

    private void init(String namespace,
                      FindersClientConfig clientConfig,
                      LoadBalancerType loadBalancerType) {
        if (namespace == null || namespace.equals("")) {
            throw new FindersRuntimeException("namespace must not be empty");
        }
        this.namespace = namespace;

        if (clientConfig == null) {
            throw new FindersRuntimeException("clientConfig must not be null");
        }
        this.config = clientConfig;

        String serverList = clientConfig.serverList();
        if (serverList == null || serverList.equals("")) {
            throw new FindersRuntimeException("serverList must not be empty");
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
        this.clientProxy = new FindersClientProxy(namespace, loadBalancer, clientConfig.requestMaxRetry());
        this.heartbeatReactor = new HeartbeatReactor(this.clientProxy, clientConfig.heartbeatThreads());
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
        return clientProxy.getAllInstances(serviceName, clusters);
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
        return clientProxy.getInstances(serviceName, clusters, healthy);
    }

    @Override
    public Instance getInstance(String serviceName, String ip, int port) {
        return getInstance(serviceName, ip, port, Services.DEFAULT_CLUSTER);
    }

    @Override
    public Instance getInstance(String serviceName, String ip, int port, String cluster) {
        return clientProxy.getInstance(serviceName, ip, port, cluster);
    }

    @Override
    public void registerInstance(String serviceName, String ip, int port) {
        registerInstance(serviceName, ip, port, Services.DEFAULT_CLUSTER);
    }

    @Override
    public void registerInstance(String serviceName, String ip, int port, String cluster) {
        clientProxy.registerInstance(serviceName, ip, port, cluster);

        Heartbeat heartbeat = new Heartbeat();
        heartbeat.setCluster(cluster);
        heartbeat.setServiceName(serviceName);
        heartbeat.setIp(ip);
        heartbeat.setPort(port);
        heartbeat.setPeriod(config.heartbeatPeriod());
        heartbeatReactor.addHeartbeat(heartbeat);
    }

    @Override
    public void registerInstance(Instance instance) {
        clientProxy.registerInstance(instance);

        Heartbeat heartbeat = new Heartbeat();
        heartbeat.setCluster(instance.getCluster());
        heartbeat.setServiceName(instance.getServiceName());
        heartbeat.setIp(instance.getIp());
        heartbeat.setPort(instance.getPort());
        heartbeat.setPeriod(config.heartbeatPeriod());
        heartbeatReactor.addHeartbeat(heartbeat);
    }

    @Override
    public void deregisterInstance(String serviceName, String ip, int port) {
        deregisterInstance(serviceName, ip, port, Services.DEFAULT_CLUSTER);
    }

    @Override
    public void deregisterInstance(String serviceName, String ip, int port, String cluster) {
        clientProxy.deregisterInstance(serviceName, ip, port, cluster);
    }

    public void shutdown() {
        this.heartbeatReactor.shutdown();
    }

}
