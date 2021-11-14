package com.dxx.finders.client;

import com.dxx.finders.client.constant.Services;
import com.dxx.finders.client.loadbalance.LoadBalancerType;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * FindersDiscovery client.
 *
 * @author dxx
 */
public class FindersDiscoveryClientTest {

    @Test
    public void testRegisterInstance() {
        String serverList = "127.0.0.1:9080,127.0.0.1:9081,127.0.0.1:9082";
        String namespace = Services.DEFAULT_NAMESPACE;
        FindersDiscoveryClient discoveryClient = new FindersDiscoveryClient(serverList, namespace,
                LoadBalancerType.ROUND);

        discoveryClient.registerInstance("testService", "127.0.0.1", 8080);
    }

    @Test
    public void testDeregisterInstance() {
        String serverList = "127.0.0.1:9080,127.0.0.1:9081,127.0.0.1:9082";
        String namespace = Services.DEFAULT_NAMESPACE;
        FindersDiscoveryClient discoveryClient = new FindersDiscoveryClient(serverList, namespace,
                LoadBalancerType.ROUND);
        Instance instance = new Instance();
        instance.setServiceName("testService");
        instance.setCluster(Services.DEFAULT_CLUSTER);
        instance.setIp("127.0.0.1");
        instance.setPort(8080);
        discoveryClient.registerInstance(instance.getServiceName(), instance);

        discoveryClient.deregisterInstance(instance.getServiceName(), instance.getIp(), instance.getPort());
    }

    @Test
    public void testGetAllInstances() {
        String serverList = "127.0.0.1:9080,127.0.0.1:9081,127.0.0.1:9082";
        String namespace = Services.DEFAULT_NAMESPACE;
        FindersDiscoveryClient discoveryClient = new FindersDiscoveryClient(serverList, namespace,
                LoadBalancerType.ROUND);
        Instance instance = new Instance();
        instance.setServiceName("testService");
        instance.setCluster(Services.DEFAULT_CLUSTER);
        instance.setIp("127.0.0.1");
        instance.setPort(8080);
        discoveryClient.registerInstance(instance.getServiceName(), instance);

        List<Instance> instanceList = discoveryClient.getAllInstances(instance.getServiceName(), instance.getCluster());
        instanceList.forEach(item -> System.out.println(item.getIp() + ":" + item.getPort()));
    }

    @Test
    public void testGetInstance() {
        String serverList = "127.0.0.1:9080,127.0.0.1:9081,127.0.0.1:9082";
        String namespace = Services.DEFAULT_NAMESPACE;
        FindersDiscoveryClient discoveryClient = new FindersDiscoveryClient(serverList, namespace,
                LoadBalancerType.ROUND);
        Instance instance = new Instance();
        instance.setServiceName("testService");
        instance.setCluster(Services.DEFAULT_CLUSTER);
        instance.setIp("127.0.0.1");
        instance.setPort(8080);
        discoveryClient.registerInstance(instance.getServiceName(), instance);

        Instance instanceInfo = discoveryClient.getInstance(instance.getServiceName(), instance.getIp(), instance.getPort());
        System.out.println(instanceInfo.getIp() + ":" + instanceInfo.getPort());
    }

}
