package com.dxx.finders.client;

import com.dxx.finders.client.constant.Paths;
import com.dxx.finders.client.constant.Services;
import com.dxx.finders.client.http.FindersHttpClient;
import com.dxx.finders.client.http.HttpMethod;
import com.dxx.finders.client.loadbalance.LoadBalancer;
import com.dxx.finders.client.util.JacksonUtils;
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

    private final int RETRY_COUNT = 3;

    private final String namespace;

    private final LoadBalancer loadBalancer;

    public FindersClientProxy(String namespace, LoadBalancer loadBalancer) {
        this.namespace = namespace;
        this.loadBalancer = loadBalancer;
    }

    public List<Instance> getAllInstances(String serviceName, List<String> clusters) {
        String clusterStr = String.join(",", clusters.toArray(new String[0]));
        String path = String.format("%s?namespace=%s&clusters=%s&serviceName=%s",
                Paths.INSTANCE_LIST, this.namespace, clusterStr, serviceName);
        String result = req(path, HttpMethod.GET, null);
        ServiceInfo serviceInfo = JacksonUtils.toObject(result, ServiceInfo.class);
        return serviceInfo.getInstances();
    }

    public List<Instance> getInstances(String serviceName, List<String> clusters, boolean healthy) {
        String clusterStr = String.join(",", clusters.toArray(new String[0]));
        String path = String.format("%s?namespace=%s&clusters=%s&serviceName=%s&healthy=%s",
                Paths.INSTANCE_LIST, this.namespace, clusterStr, serviceName, healthy);
        String result = req(path, HttpMethod.GET, null);
        ServiceInfo serviceInfo = JacksonUtils.toObject(result, ServiceInfo.class);
        return serviceInfo.getInstances();
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

    private String req(String path, HttpMethod method, String body) {
        String server = loadBalancer.chooseServer();
        for (int i = 0; i < RETRY_COUNT; i++) {
            try {
                String url = String.format("http://%s%s", server, path);
                return FindersHttpClient.request(url, method, body, null);
            } catch (FindersRuntimeException e) {
                LOGGER.error("Error while request server {}: ", server,  e);
                server = loadBalancer.chooseServer();
            }
        }
        throw new FindersRuntimeException("Get instances failed, server: " + server);
    }
}
