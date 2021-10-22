package com.dxx.finders.cluster;

import com.dxx.finders.config.ClusterConfig;
import com.dxx.finders.config.ConfigHolder;
import com.dxx.finders.exception.FindersRuntimeException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Cluster node management.
 *
 * @author dxx
 */
public class ServerNodeManager {

    private static final ServerNodeManager INSTANCE = new ServerNodeManager();

    private volatile Map<String, ServerNode> allNodes = new HashMap<>();

    private String selfId;

    private ServerNodeManager() {}

    public static ServerNodeManager init() {
        ClusterConfig clusterConfig = ConfigHolder.config().getClusterConfig();
        if (clusterConfig != null) {
            INSTANCE.selfId = clusterConfig.getSelfId();
            if (clusterConfig.getNodes() != null) {
                INSTANCE.allNodes = clusterConfig.getNodes().stream().map(INSTANCE::parseNode)
                        .collect(Collectors.toMap(ServerNode::getId, Function.identity(), (oldValue, newValue) -> {
                            throw new FindersRuntimeException(String.format("Duplicate cluster node %s", newValue));
                        }));
            }
        }
        return INSTANCE;
    }

    public List<ServerNode> allNodes() {
        return new ArrayList<>(allNodes.values());
    }

    public List<ServerNode> allNodesWithoutSelf() {
        Map<String, ServerNode> allNodes = new HashMap<>(this.allNodes);
        allNodes.remove(selfId);
        return new ArrayList<>(allNodes.values());
    }

    public ServerNode selfNode() {
        return this.allNodes.get(this.selfId);
    }

    private ServerNode parseNode(String node) {
        String[] nodeInfo = node.split("-");
        if (nodeInfo.length > 1) {
            String address = nodeInfo[1];
            String[] addressInfo = address.split(":");
            int port = 9080;
            if (addressInfo.length > 1) {
                address = addressInfo[0];
                port = Integer.parseInt(addressInfo[1]);
            }
            return ServerNode.builder().id(nodeInfo[0]).ip(address).port(port).build();
        }
        throw new FindersRuntimeException(String.format("The cluster node format (%s) is incorrect", node));
    }

}
