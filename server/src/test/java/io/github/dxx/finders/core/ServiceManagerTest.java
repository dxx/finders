package io.github.dxx.finders.core;

import io.github.dxx.finders.cluster.ServerNodeManager;
import io.github.dxx.finders.constant.Services;
import org.junit.jupiter.api.Test;

/**
 * ServiceManager test.
 *
 * @author dxx
 */
public class ServiceManagerTest {

    public ServiceManager init() {
        ServerNodeManager serverNodeManager = ServerNodeManager.init();
        SyncManager syncManager = new SyncManager(serverNodeManager);

        ServiceManager serviceManager = new ServiceManager(syncManager);
        syncManager.init(serviceManager);
        return serviceManager;
    }

    @Test
    public void testRegisterInstance() {
        ServiceManager serviceManager = init();

        Instance instance = new Instance();
        instance.setServiceName("serviceTest");
        instance.setCluster(Services.DEFAULT_CLUSTER);
        instance.setIp("127.0.0.1");
        instance.setPort(9080);

        serviceManager.registerInstance(Services.DEFAULT_NAMESPACE, instance.getServiceName(), instance);
    }

    @Test
    public void testDeregisterInstance() {
        ServiceManager serviceManager = init();

        Instance instance = new Instance();
        instance.setServiceName("serviceTest");
        instance.setCluster(Services.DEFAULT_CLUSTER);
        instance.setIp("127.0.0.1");
        instance.setPort(9080);

        serviceManager.registerInstance(Services.DEFAULT_NAMESPACE, instance.getServiceName(), instance);

        serviceManager.deregisterInstance(Services.DEFAULT_NAMESPACE, instance.getServiceName(), instance);
    }

}
