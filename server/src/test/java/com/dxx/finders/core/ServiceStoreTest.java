package com.dxx.finders.core;

import com.dxx.finders.constant.Services;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * ServiceStore test.
 *
 * @author dxx
 */
public class ServiceStoreTest {

    @Test
    public void testCheckInfo() {
        List<Instance> instanceList = new ArrayList<>();
        Instance instance = new Instance();
        instance.setServiceName("testService");
        instance.setCluster(Services.DEFAULT_CLUSTER);
        instance.setIp("127.0.0.1");
        instance.setPort(9080);
        instanceList.add(instance);

        ServiceStore serviceStore = new ServiceStore();
        String key = ServiceKey.build(Services.DEFAULT_NAMESPACE, instance.getServiceName());
        serviceStore.put(key, instanceList);

        String checkInfo = serviceStore.getCheckInfo(key);
        System.out.println(checkInfo);
    }

}
