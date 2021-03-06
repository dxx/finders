package io.github.dxx.finders.client;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.dxx.finders.client.constant.Paths;
import io.github.dxx.finders.client.constant.Services;
import io.github.dxx.finders.client.http.FindersHttpClient;
import io.github.dxx.finders.client.http.HttpMethod;
import io.github.dxx.finders.client.loadbalance.LoadBalancer;
import io.github.dxx.finders.client.model.Heartbeat;
import io.github.dxx.finders.client.model.Instance;
import io.github.dxx.finders.client.model.ServiceInfo;
import io.github.dxx.finders.client.util.JacksonUtils;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Finders client proxy.
 *
 * @author dxx
 */
public class FindersClientProxy {

    private static final Logger LOGGER = LoggerFactory.getLogger(FindersClientProxy.class);

    private final int retryCount;

    private final String namespace;

    private final LoadBalancer loadBalancer;

    public FindersClientProxy(String namespace, LoadBalancer loadBalancer, int maxRetry) {
        this.namespace = namespace;
        this.loadBalancer = loadBalancer;
        this.retryCount = maxRetry;
    }

    public ServiceInfo getService(String serviceName, List<String> clusters) {
        String clusterStr = String.join(",", clusters.toArray(new String[]{}));
        String path = String.format("%s?namespace=%s&clusters=%s&serviceName=%s",
                Paths.INSTANCE_LIST, this.namespace, clusterStr, serviceName);
        String result = req(path, HttpMethod.GET, null);
        return JacksonUtils.toObject(result, ServiceInfo.class);
    }

    public Instance getInstance(String serviceName, String ip, int port, String cluster) {
        String path = String.format("%s?namespace=%s&cluster=%s&serviceName=%s&ip=%s&port=%s",
                Paths.INSTANCE, this.namespace, cluster, serviceName, ip, port);
        String result = req(path, HttpMethod.GET, null);
        return JacksonUtils.toObject(result, Instance.class);
    }

    public void registerInstance(String serviceName, String ip, int port, String cluster) {
        ObjectNode objectNode = JacksonUtils.createObjectNode();
        objectNode.put(Services.NAMESPACE, this.namespace);
        objectNode.put(Services.CLUSTER, cluster);
        objectNode.put(Services.SERVICE_NAME, serviceName);
        objectNode.put("ip", ip);
        objectNode.put("port", port);

        req(Paths.INSTANCE, HttpMethod.POST, objectNode.toString());
    }

    public void registerInstance(Instance instance) {
        req(Paths.INSTANCE, HttpMethod.POST, JacksonUtils.toJson(instance));
    }

    public void deregisterInstance(String serviceName, String ip, int port, String cluster) {
        ObjectNode objectNode = JacksonUtils.createObjectNode();
        objectNode.put(Services.NAMESPACE, this.namespace);
        objectNode.put(Services.CLUSTER, cluster);
        objectNode.put(Services.SERVICE_NAME, serviceName);
        objectNode.put("ip", ip);
        objectNode.put("port", port);

        req(Paths.INSTANCE, HttpMethod.DELETE, objectNode.toString());
    }

    public void updateInstanceStatus(Instance instance) {
        req(Paths.INSTANCE_STATUS, HttpMethod.PUT, JacksonUtils.toJson(instance));
    }

    public List<String> getServiceNames() {
        String path = String.format("%s?namespace=%s",
                Paths.SERVICE_NAMES, this.namespace);
        String result = req(path, HttpMethod.GET, null);
        return JacksonUtils.toObject(result, new TypeReference<List<String>>() {});
    }

    public boolean serverHealth() {
        String result = req(Paths.SERVER_HEALTH, HttpMethod.GET, null);
        return result != null && !result.equals("");
    }

    public void sendHeartbeat(Heartbeat heartbeat) {
        ObjectNode objectNode = JacksonUtils.createObjectNode();
        objectNode.put(Services.NAMESPACE, this.namespace);
        objectNode.put(Services.CLUSTER, heartbeat.getCluster());
        objectNode.put(Services.SERVICE_NAME, heartbeat.getServiceName());
        objectNode.put("ip", heartbeat.getIp());
        objectNode.put("port", heartbeat.getPort());
        req(Paths.INSTANCE_BEAT, HttpMethod.PUT, objectNode.toString());
    }

    private String req(String path, HttpMethod method, String body) {
        String server = loadBalancer.chooseServer();
        for (int i = 0; i <= retryCount; i++) {
            try {
                String url = String.format("http://%s%s", server, path);
                return FindersHttpClient.request(url, method, body, null);
            } catch (FindersRuntimeException e) {
                LOGGER.error(String.format("Error while request server %s", server), e);
                server = loadBalancer.chooseServer();
            }
        }
        throw new FindersRuntimeException(String.format("Request server %s failed", server));
    }
}
