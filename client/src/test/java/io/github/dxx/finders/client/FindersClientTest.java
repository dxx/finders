package io.github.dxx.finders.client;

import io.github.dxx.finders.client.constant.Services;
import io.github.dxx.finders.client.loadbalance.LoadBalancerType;
import io.github.dxx.finders.client.model.Instance;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

/**
 * FindersClient test.
 *
 * @author dxx
 */
public class FindersClientTest {

    private static FindersClient findersClient;

    @BeforeAll
    public static void setUp() {
        String namespace = Services.DEFAULT_NAMESPACE;
        String serverList = "127.0.0.1:9080,127.0.0.1:9081,127.0.0.1:9082";
        FindersClientConfig findersClientConfig = FindersClientConfig.builder()
                .serverList(serverList)
                .requestMaxRetry(3)
                .servicePullThreads(1)
                .heartbeatThreads(1)
                .heartbeatPeriod(5000)
                .build();
        findersClient = new FindersClient(namespace, findersClientConfig,
                LoadBalancerType.ROUND);
    }

    @Test
    public void testRegisterInstance() throws IOException {
        findersClient.registerInstance("testService", "127.0.0.1", 8080);

        System.in.read();

        findersClient.close();
    }

    @Test
    public void testDeregisterInstance() {
        Instance instance = new Instance();
        instance.setServiceName("testService");
        instance.setCluster(Services.DEFAULT_CLUSTER);
        instance.setIp("127.0.0.1");
        instance.setPort(8080);
        findersClient.registerInstance(instance);

        findersClient.deregisterInstance(instance.getServiceName(), instance.getIp(), instance.getPort());

        findersClient.close();
    }

    @Test
    public void testGetAllInstances() throws IOException {
        Instance instance = new Instance();
        instance.setServiceName("testService");
        instance.setCluster(Services.DEFAULT_CLUSTER);
        instance.setIp("127.0.0.1");
        instance.setPort(8080);
        findersClient.registerInstance(instance);

        List<Instance> instanceList = findersClient.getAllInstances(instance.getServiceName(), instance.getCluster());
        instanceList.forEach(item -> System.out.println(item.getIp() + ":" + item.getPort()));

        findersClient.close();
    }

    @Test
    public void testGetInstance() {
        Instance instance = new Instance();
        instance.setServiceName("testService");
        instance.setCluster(Services.DEFAULT_CLUSTER);
        instance.setIp("127.0.0.1");
        instance.setPort(8080);
        findersClient.registerInstance(instance);

        Instance instanceInfo = findersClient.getInstance(instance.getServiceName(), instance.getIp(), instance.getPort());
        System.out.println(instanceInfo.getIp() + ":" + instanceInfo.getPort());

        findersClient.close();
    }

}
