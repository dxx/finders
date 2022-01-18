package io.github.dxx.finders.client;

import io.github.dxx.finders.client.constant.Services;
import io.github.dxx.finders.client.loadbalance.LoadBalancer;
import io.github.dxx.finders.client.loadbalance.LoadBalancerType;
import io.github.dxx.finders.client.loadbalance.RandomBalancer;
import io.github.dxx.finders.client.loadbalance.RoundBalancer;
import io.github.dxx.finders.client.model.Heartbeat;
import io.github.dxx.finders.client.model.Instance;
import io.github.dxx.finders.client.model.InstanceStatus;
import io.github.dxx.finders.client.model.ServiceInfo;
import io.github.dxx.finders.client.reactor.HeartbeatReactor;
import io.github.dxx.finders.client.reactor.ServiceReactor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Finders client.
 *
 * @author dxx
 */
public class FindersClient implements FindersClientService {

    private FindersClientConfig config;

    private FindersClientProxy clientProxy;

    private ServiceReactor serviceReactor;

    private HeartbeatReactor heartbeatReactor;

    public FindersClient(String namespace,
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
        this.serviceReactor = new ServiceReactor(this.clientProxy, clientConfig.servicePullThreads(),
                clientConfig.servicePullPeriod());
        this.heartbeatReactor = new HeartbeatReactor(this.clientProxy, clientConfig.heartbeatThreads());
    }

    @Override
    public List<Instance> getAllInstances(String serviceName) {
        return getAllInstances(serviceName, Collections.emptyList());
    }

    @Override
    public List<Instance> getAllInstances(String serviceName, String cluster) {
        return getAllInstances(serviceName, Collections.singletonList(cluster));
    }

    @Override
    public List<Instance> getAllInstances(String serviceName, List<String> clusters) {
        return getInstances(serviceName, clusters, false);
    }

    @Override
    public List<Instance> getInstances(String serviceName, boolean healthyOnly) {
        return getInstances(serviceName, Services.DEFAULT_CLUSTER, healthyOnly);
    }

    @Override
    public List<Instance> getInstances(String serviceName, String cluster, boolean healthyOnly) {
        return getInstances(serviceName, Collections.singletonList(cluster), healthyOnly);
    }

    @Override
    public List<Instance> getInstances(String serviceName, List<String> clusters, boolean healthyOnly) {
        return selectInstances(serviceName, clusters, healthyOnly);
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
        heartbeatReactor.removeHeartbeat(cluster, serviceName, ip, port);
        clientProxy.deregisterInstance(serviceName, ip, port, cluster);
    }

    @Override
    public void updateInstance(String serviceName, String cluster, Instance instance) {
        if (instance != null) {
            instance.setServiceName(serviceName);
            instance.setCluster(cluster);
        }
        clientProxy.updateInstanceStatus(instance);
    }

    @Override
    public List<String> getServiceNames() {
        return clientProxy.getServiceNames();
    }

    private List<Instance> selectInstances(String serviceName, List<String> clusters, boolean healthyOnly) {
        ServiceInfo serviceInfo = serviceReactor.getService(serviceName, clusters);
        if (serviceInfo == null) {
            return new ArrayList<>();
        }
        if (healthyOnly) {
            return serviceInfo.getInstances().stream()
                    .filter(item -> item.getStatus() == InstanceStatus.HEALTHY).collect(Collectors.toList());
        }
        return serviceInfo.getInstances();
    }

    @Override
    public void close() {
        this.serviceReactor.shutdown();
        this.heartbeatReactor.shutdown();
    }

}
