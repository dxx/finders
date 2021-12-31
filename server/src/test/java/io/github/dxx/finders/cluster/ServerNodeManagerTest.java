package io.github.dxx.finders.cluster;

import io.github.dxx.finders.env.EnvConst;
import io.github.dxx.finders.env.Environment;
import io.github.dxx.finders.notify.Notifier;
import io.github.dxx.finders.util.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * ServerNodeManager test.
 *
 * @author dxx
 */
public class ServerNodeManagerTest {

    @BeforeAll
    public static void init() {
        String home = System.getProperty(EnvConst.HOME);
        home = StringUtils.defaultIfEmpty(home, System.getProperty("user.dir"));
        System.setProperty(EnvConst.HOME, home);

        Environment.init();
    }

    @Test
    public void testAllNodes() {
        ServerNodeManager serverNodeManager = ServerNodeManager.init();
        List<ServerNode> serverNodes = serverNodeManager.allNodes();
        serverNodes.forEach(System.out::println);
    }

    @Test
    public void testUpNodesWithoutSelf() {
        ServerNodeManager serverNodeManager = ServerNodeManager.init();
        List<ServerNode> serverNodes = serverNodeManager.upNodesWithoutSelf();
        serverNodes.forEach(System.out::println);
    }

    @Test
    public void testServerUpChange() throws InterruptedException {
        ServerNodeManager serverNodeManager = ServerNodeManager.init();
        Notifier.registerSubscriber(serverNodeManager);

        System.out.println("Before server change.");

        List<ServerNode> serverNodes = serverNodeManager.allNodes();
        serverNodes.forEach(System.out::println);

        ServerChangeEvent serverChangeEvent = new ServerChangeEvent();
        serverChangeEvent.setId("n1");
        serverChangeEvent.setAddress("127.0.0.1:9000");
        serverChangeEvent.setStatus(ServerStatus.UP);

        Notifier.publishEvent(serverChangeEvent);

        TimeUnit.SECONDS.sleep(1);

        System.out.println("After server change.");

        serverNodes = serverNodeManager.allNodes();
        serverNodes.forEach(System.out::println);
    }

    @Test
    public void testServerDownChange() throws InterruptedException {
        ServerNodeManager serverNodeManager = ServerNodeManager.init();
        Notifier.registerSubscriber(serverNodeManager);

        System.out.println("Before server change.");

        List<ServerNode> serverNodes = serverNodeManager.allNodes();
        serverNodes.forEach(System.out::println);

        ServerChangeEvent serverChangeEvent = new ServerChangeEvent();
        serverChangeEvent.setId("n1");
        serverChangeEvent.setAddress("127.0.0.1:9081");
        serverChangeEvent.setStatus(ServerStatus.DOWN);

        Notifier.publishEvent(serverChangeEvent);

        TimeUnit.SECONDS.sleep(1);

        System.out.println("After server change.");

        serverNodes = serverNodeManager.allNodes();
        serverNodes.forEach(System.out::println);

        serverNodes = serverNodeManager.upNodesWithoutSelf();
        serverNodes.forEach(System.out::println);
    }

}
