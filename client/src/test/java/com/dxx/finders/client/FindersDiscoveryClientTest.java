package com.dxx.finders.client;

import com.dxx.finders.client.constant.Services;
import com.dxx.finders.client.loadbalance.LoadBalancerType;
import com.dxx.finders.client.model.Instance;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

/**
 * FindersDiscovery client.
 *
 * @author dxx
 */
public class FindersDiscoveryClientTest {

    @Test
    public void testRegisterInstance() throws IOException {
        String namespace = Services.DEFAULT_NAMESPACE;
        String serverList = "127.0.0.1:9080,127.0.0.1:9081,127.0.0.1:9082";
        FindersClientConfig findersClientConfig = FindersClientConfig.builder()
                .serverList(serverList)
                .requestMaxRetry(3)
                .heartbeatThreads(3)
                .heartbeatPeriod(5000)
                .build();
        FindersDiscoveryClient discoveryClient = new FindersDiscoveryClient(namespace, findersClientConfig,
                LoadBalancerType.ROUND);

        discoveryClient.registerInstance("testService", "127.0.0.1", 8080);

        System.in.read();

        discoveryClient.shutdown();
    }

    @Test
    public void testDeregisterInstance() {
        String namespace = Services.DEFAULT_NAMESPACE;
        String serverList = "127.0.0.1:9080,127.0.0.1:9081,127.0.0.1:9082";
        FindersClientConfig findersClientConfig = FindersClientConfig.builder()
                .serverList(serverList).build();
        FindersDiscoveryClient discoveryClient = new FindersDiscoveryClient(namespace, findersClientConfig,
                LoadBalancerType.ROUND);
        Instance instance = new Instance();
        instance.setServiceName("testService");
        instance.setCluster(Services.DEFAULT_CLUSTER);
        instance.setIp("127.0.0.1");
        instance.setPort(8080);
        discoveryClient.registerInstance(instance);

        discoveryClient.deregisterInstance(instance.getServiceName(), instance.getIp(), instance.getPort());

        discoveryClient.shutdown();
    }

    @Test
    public void testGetAllInstances() {
        String namespace = Services.DEFAULT_NAMESPACE;
        String serverList = "127.0.0.1:9080,127.0.0.1:9081,127.0.0.1:9082";
        FindersClientConfig findersClientConfig = FindersClientConfig.builder()
                .serverList(serverList).build();
        FindersDiscoveryClient discoveryClient = new FindersDiscoveryClient(namespace, findersClientConfig,
                LoadBalancerType.ROUND);
        Instance instance = new Instance();
        instance.setServiceName("testService");
        instance.setCluster(Services.DEFAULT_CLUSTER);
        instance.setIp("127.0.0.1");
        instance.setPort(8080);
        discoveryClient.registerInstance(instance);

        List<Instance> instanceList = discoveryClient.getAllInstances(instance.getServiceName(), instance.getCluster());
        instanceList.forEach(item -> System.out.println(item.getIp() + ":" + item.getPort()));

        discoveryClient.shutdown();
    }

    @Test
    public void testGetInstance() {
        String namespace = Services.DEFAULT_NAMESPACE;
        String serverList = "127.0.0.1:9080,127.0.0.1:9081,127.0.0.1:9082";
        FindersClientConfig findersClientConfig = FindersClientConfig.builder()
                .serverList(serverList).build();
        FindersDiscoveryClient discoveryClient = new FindersDiscoveryClient(namespace, findersClientConfig,
                LoadBalancerType.ROUND);
        Instance instance = new Instance();
        instance.setServiceName("testService");
        instance.setCluster(Services.DEFAULT_CLUSTER);
        instance.setIp("127.0.0.1");
        instance.setPort(8080);
        discoveryClient.registerInstance(instance);

        Instance instanceInfo = discoveryClient.getInstance(instance.getServiceName(), instance.getIp(), instance.getPort());
        System.out.println(instanceInfo.getIp() + ":" + instanceInfo.getPort());

        discoveryClient.shutdown();
    }

}
