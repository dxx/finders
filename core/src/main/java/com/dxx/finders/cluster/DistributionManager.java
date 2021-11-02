package com.dxx.finders.cluster;

import com.dxx.finders.exception.ArgumentException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Distribution management.
 *
 * @author dxx
 */
public class DistributionManager {

    private static final DistributionManager INSTANCE = new DistributionManager();

    private volatile List<String> nodeList = new ArrayList<>();

    private ServerNodeManager serverNodeManager;

    private DistributionManager() {}

    public static void init(ServerNodeManager serverNodeManager) {
        INSTANCE.serverNodeManager = serverNodeManager;
        INSTANCE.nodeList = serverNodeManager.allNodes().stream()
                .map(ServerNode::getAddress).distinct().sorted().collect(Collectors.toList());
    }

    public static boolean isCluster() {
        checkServerNodeManager();
        return INSTANCE.serverNodeManager.selfNode() != null
                && INSTANCE.serverNodeManager.allNodes().size() > 1;
    }

    public static boolean isResponsible(String serviceName) {
        checkServerNodeManager();
        int index = INSTANCE.distributedIndex(serviceName);
        return INSTANCE.serverNodeManager.selfNode().getAddress().equals(INSTANCE.nodeList.get(index));
    }

    public static String getDistributedAddress(String serviceName) {
        checkServerNodeManager();
        int index = INSTANCE.distributedIndex(serviceName);
        return INSTANCE.nodeList.get(index);
    }

    private int distributedIndex(String serviceName) {
        return Math.abs(INSTANCE.hash(serviceName)) % INSTANCE.nodeList.size();
    }

    /**
     * See {@link HashMap} hash method.
     */
    private int hash(String key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    private static void checkServerNodeManager() {
        if (INSTANCE.serverNodeManager == null) {
            throw new ArgumentException("ServerNodeManager must be initialize");
        }
    }

}
