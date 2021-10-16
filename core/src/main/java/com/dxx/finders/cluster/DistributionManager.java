package com.dxx.finders.cluster;

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

    public static boolean isResponsible(String serviceName) {
        int index = Math.abs(INSTANCE.hash(serviceName)) % INSTANCE.nodeList.size();
        return INSTANCE.serverNodeManager.getSelfNode().getAddress().equals(INSTANCE.nodeList.get(index));
    }

    /**
     * See {@link HashMap} hash method.
     */
    private int hash(String key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

}
