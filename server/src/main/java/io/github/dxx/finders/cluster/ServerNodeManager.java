package io.github.dxx.finders.cluster;

import io.github.dxx.finders.config.ClusterConfig;
import io.github.dxx.finders.config.ConfigHolder;
import io.github.dxx.finders.config.ServerConfig;
import io.github.dxx.finders.exception.FindersRuntimeException;
import io.github.dxx.finders.notify.Notifier;

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
public class ServerNodeManager extends ServerChangeSubscriber {

    private static final ServerNodeManager INSTANCE = new ServerNodeManager();

    private volatile Map<String, ServerNode> allNodes = new HashMap<>();

    /**
     * Online list of service addresses, must be unrepeatable and orderly.
     */
    private volatile List<String> upAddresses = new ArrayList<>();

    private static final int DEFAULT_PORT = 9080;

    private static final String LOCALHOST_IP = "127.0.0.1";

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
                INSTANCE.upAddresses = INSTANCE.allNodes.values().stream()
                        .map(ServerNode::getAddress).distinct().sorted().collect(Collectors.toList());
            }
        }

        Notifier.registerSubscriber(INSTANCE);

        return INSTANCE;
    }

    public List<ServerNode> allNodes() {
        return new ArrayList<>(this.allNodes.values());
    }

    public ServerNode selfNode() {
        return this.allNodes.get(this.selfId);
    }

    public List<ServerNode> upNodesWithoutSelf() {
        Map<String, ServerNode> allNodes = new HashMap<>(this.allNodes);
        allNodes.remove(selfId);
        return allNodes.values().stream().filter(item -> item.getStatus() == ServerStatus.UP)
                .collect(Collectors.toList());
    }

    public List<String> getUpAddresses() {
        return this.upAddresses;
    }

    public static ServerNode getLocalNode() {
        if (INSTANCE.selfNode() != null) {
            return INSTANCE.selfNode();
        }
        ServerConfig serverConfig = ConfigHolder.config().getServerConfig();
        return ServerNode.builder().id("local").ip(LOCALHOST_IP).port(serverConfig.getPort()).build();
    }

    private ServerNode parseNode(String node) {
        String[] nodeInfo = node.split("-");
        if (nodeInfo.length > 1) {
            String address = nodeInfo[1];
            String[] addressInfo = address.split(":");
            int port = DEFAULT_PORT;
            if (addressInfo.length > 1) {
                address = addressInfo[0];
                port = Integer.parseInt(addressInfo[1]);
            }
            return ServerNode.builder().id(nodeInfo[0]).ip(address).port(port).build();
        }
        throw new FindersRuntimeException(String.format("The cluster node format (%s) is incorrect", node));
    }

    @Override
    public void onEvent(ServerChangeEvent event) {
        String[] address = event.getAddress().split(":");
        ServerNode serverNode = ServerNode.builder().id(event.getId())
                .ip(address[0]).port(Integer.parseInt(address[1])).status(event.getStatus()).build();
        this.allNodes.put(serverNode.getId(), serverNode);

        this.upAddresses = this.allNodes.values().stream().filter(item -> item.getStatus() == ServerStatus.UP)
                .map(ServerNode::getAddress).distinct().sorted().collect(Collectors.toList());
    }

}
