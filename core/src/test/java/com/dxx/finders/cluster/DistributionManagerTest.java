package com.dxx.finders.cluster;

import com.dxx.finders.env.EnvConst;
import com.dxx.finders.env.Environment;
import com.dxx.finders.util.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * DistributionManager test.
 *
 * @author dxx
 */
public class DistributionManagerTest {

    @BeforeAll
    public static void init() {
        String home = System.getProperty(EnvConst.HOME);
        home = StringUtils.defaultIfEmpty(home, System.getProperty("user.dir"));
        System.setProperty(EnvConst.HOME, home);

        Environment.init();
    }

    @Test
    public void testIsCluster() {
        ServerNodeManager serverNodeManager = ServerNodeManager.init();
        DistributionManager.init(serverNodeManager);

        boolean isCluster = DistributionManager.isCluster();
        System.out.println(isCluster);
    }

    @Test
    public void testIsResponsible() {
        ServerNodeManager serverNodeManager = ServerNodeManager.init();
        DistributionManager.init(serverNodeManager);

        String serviceName = "testService";

        boolean isResponsible = DistributionManager.isResponsible(serviceName);
        System.out.println(isResponsible);
    }
}
