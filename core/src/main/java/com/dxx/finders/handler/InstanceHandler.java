package com.dxx.finders.handler;

import com.dxx.finders.constant.Services;
import com.dxx.finders.constant.Paths;
import com.dxx.finders.constant.Result;
import com.dxx.finders.core.Instance;
import com.dxx.finders.core.InstanceStatus;
import com.dxx.finders.core.Service;
import com.dxx.finders.core.ServiceManager;
import com.dxx.finders.exception.ValidationException;
import com.dxx.finders.http.RequestMethod;
import com.dxx.finders.http.annotation.Distribute;
import com.dxx.finders.http.annotation.RequestMapping;
import com.dxx.finders.util.JacksonUtils;
import com.dxx.finders.util.ParamUtils;
import com.dxx.finders.util.StringUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

import java.util.*;

/**
 * Service handler.
 *
 * @author dxx
 */
public class InstanceHandler {

    private final ServiceManager serviceManager;

    public InstanceHandler(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    /**
     * Get list of service instance.
     */
    @RequestMapping(path = Paths.INSTANCE_LIST, method = RequestMethod.GET)
    public void list(RoutingContext context) {
        HttpServerRequest request = context.request();
        HttpServerResponse response = context.response();

        String namespace = ParamUtils.optional(request, Services.PARAM_NAMESPACE, Services.DEFAULT_NAMESPACE);
        String serviceName = ParamUtils.required(request, Services.PARAM_SERVICE_NAME);
        String clusters = ParamUtils.optional(request, "clusters", "");

        ObjectNode node = doList(namespace, clusters, serviceName);

        response.putHeader(HttpHeaderNames.CONTENT_TYPE,HttpHeaderValues.APPLICATION_JSON + ";charset=UTF-8");
        response.end(JacksonUtils.toJson(node));
    }

    /**
     * Get detail of service instance.
     */
    @RequestMapping(path = Paths.INSTANCE, method = RequestMethod.GET)
    public void detail(RoutingContext context) {
        HttpServerRequest request = context.request();
        HttpServerResponse response = context.response();

        Instance instance = doDetail(request);
        response.putHeader(HttpHeaderNames.CONTENT_TYPE,HttpHeaderValues.APPLICATION_JSON + ";charset=UTF-8");
        response.end(JacksonUtils.toJson(instance));
    }

    /**
     * Register instance.
     */
    @Distribute
    @RequestMapping(path = Paths.INSTANCE, method = RequestMethod.POST)
    public void register(RoutingContext context) {
        HttpServerResponse response = context.response();

        JsonNode jsonNode = ParamUtils.getBodyAsJsonNode(context);
        String namespace = jsonNode.get(Services.PARAM_NAMESPACE) != null ?
                jsonNode.get(Services.PARAM_NAMESPACE).asText() : Services.DEFAULT_NAMESPACE;

        Instance instance = createInstance(jsonNode.toString());

        serviceManager.registerInstance(namespace, instance.getServiceName(), instance);

        response.end(Result.SUCCESS);
    }

    /**
     * Deregister instance.
     */
    @Distribute
    @RequestMapping(path = Paths.INSTANCE, method = RequestMethod.DELETE)
    public void deregister(RoutingContext context) {
        HttpServerResponse response = context.response();

        JsonNode jsonNode = ParamUtils.getBodyAsJsonNode(context);
        String namespace = jsonNode.get(Services.PARAM_NAMESPACE) != null ?
                jsonNode.get(Services.PARAM_NAMESPACE).asText() : Services.DEFAULT_NAMESPACE;

        Instance instance = createInstance(jsonNode.toString());

        Service service = serviceManager.getService(namespace, instance.getServiceName());
        if (service == null) {
            response.end(Result.SUCCESS);
            return;
        }

        serviceManager.deregisterInstance(namespace, instance.getServiceName(), instance);

        response.end(Result.SUCCESS);
    }

    /**
     * Send a heartbeat to the specified instance.
     */
    @Distribute
    @RequestMapping(path = Paths.INSTANCE_BEAT, method = RequestMethod.PUT)
    public void beat(RoutingContext context) {
        HttpServerResponse response = context.response();

        JsonNode jsonNode = ParamUtils.getBodyAsJsonNode(context);
        String namespace = jsonNode.get(Services.PARAM_NAMESPACE) != null ?
                jsonNode.get(Services.PARAM_NAMESPACE).asText() : Services.DEFAULT_NAMESPACE;

        Instance paramInstance = createInstance(jsonNode.toString());

        Instance instance = serviceManager.getInstance(namespace, paramInstance.getServiceName(),
                paramInstance.getCluster(), paramInstance.getIp(), paramInstance.getPort());

        if (instance == null) {
            serviceManager.registerInstance(namespace, paramInstance.getServiceName(), paramInstance);
        }
        serviceManager.handleInstanceHeartbeat(namespace, paramInstance.getServiceName(),
                paramInstance.getCluster(), paramInstance.getIp(), paramInstance.getPort());

        response.end(Result.SUCCESS);
    }

    /**
     * Update status of instance.
     */
    @Distribute
    @RequestMapping(path = Paths.INSTANCE_STATUS, method = RequestMethod.PUT)
    public void status(RoutingContext context) {
        HttpServerResponse response = context.response();

        JsonNode jsonNode = ParamUtils.getBodyAsJsonNode(context);
        String namespace = jsonNode.get(Services.PARAM_NAMESPACE) != null ?
                jsonNode.get(Services.PARAM_NAMESPACE).asText() : Services.DEFAULT_NAMESPACE;
        String cluster = jsonNode.get(Services.PARAM_CLUSTER) != null ?
                jsonNode.get(Services.PARAM_CLUSTER).asText() : Services.DEFAULT_CLUSTER;
        String serviceName = jsonNode.get(Services.PARAM_SERVICE_NAME) != null ?
                jsonNode.get(Services.PARAM_SERVICE_NAME).asText() : "";
        ParamUtils.requiredCheck(Services.PARAM_SERVICE_NAME, serviceName);

        String ip = jsonNode.get("ip") != null ? jsonNode.get("ip").asText() : "";
        String port = jsonNode.get("port") != null ? jsonNode.get("port").asText() : "";
        String status = jsonNode.get("status") != null ? jsonNode.get("status").asText() : "";

        ParamUtils.requiredCheck("ip", ip);
        ParamUtils.requiredCheck("port", port);
        ParamUtils.requiredCheck("status", status);

        Instance instance = new Instance();
        instance.setCluster(cluster);
        instance.setServiceName(serviceName);
        instance.setIp(ip);
        instance.setPort(Integer.parseInt(port));
        instance.setStatus(InstanceStatus.fromStr(status));

        serviceManager.updateInstanceStatus(namespace, serviceName, instance);
        response.end(Result.SUCCESS);
    }

    private Instance createInstance(String json) {
        Instance instance = JacksonUtils.toObject(json, Instance.class);
        ParamUtils.requiredCheck(Services.PARAM_SERVICE_NAME, instance.getServiceName());
        ParamUtils.requiredCheck("ip", instance.getIp());
        instance.setCluster(StringUtils.defaultIfEmpty(instance.getCluster(), Services.DEFAULT_CLUSTER));

        if (!instance.getCluster().matches(Services.CLUSTER_SYNTAX)) {
            throw new ValidationException(HttpResponseStatus.BAD_REQUEST.code(),
                    "Cluster name can only have these characters: 0-9a-zA-Z-_");
        }
        if (!instance.getServiceName().matches(Services.SERVICE_NAME_SYNTAX)) {
            throw new ValidationException(HttpResponseStatus.BAD_REQUEST.code(),
                    "Service name can only have these characters: 0-9a-zA-Z@.:_-");
        }
        if (instance.getPort() == 0) {
            throw new ValidationException(HttpResponseStatus.BAD_REQUEST.code(),
                    "Param 'port' is required and must be greater than zero");
        }

        instance.createInstanceId();
        return instance;
    }

    private ObjectNode doList(String namespace, String clusters, String serviceName) {
        List<String> clusterNames = new ArrayList<>();
        if (StringUtils.isNotEmpty(clusters)) {
            clusterNames = Arrays.asList(clusters.split(","));
        }
        Service service = serviceManager.getService(namespace, serviceName);

        ObjectNode objectNode = JacksonUtils.createObjectNode();
        objectNode.put(Services.PARAM_SERVICE_NAME, serviceName);
        ArrayNode clusterArrayNode = JacksonUtils.createArrayNode();
        ArrayNode instanceArrayNode = JacksonUtils.createArrayNode();

        objectNode.set("clusters", clusterArrayNode);
        objectNode.set("instances", instanceArrayNode);

        if (service == null) {
            clusterNames.forEach(clusterArrayNode::add);
            return objectNode;
        }

        List<Instance> instances;
        if (clusterNames.size() > 0) {
            clusterNames.forEach(clusterArrayNode::add);
            instances = service.getInstances(clusterNames);
        } else {
            instances = service.getInstances();
            instances.stream().map(Instance::getCluster).distinct().forEach(clusterArrayNode::add);
        }

        instances.forEach(instanceArrayNode::addPOJO);

        return objectNode;
    }

    public Instance doDetail(HttpServerRequest request) {
        String namespace = ParamUtils.optional(request, Services.PARAM_NAMESPACE, Services.DEFAULT_NAMESPACE);
        String cluster = ParamUtils.optional(request, Services.PARAM_CLUSTER, Services.DEFAULT_CLUSTER);
        String serviceName = ParamUtils.required(request, Services.PARAM_SERVICE_NAME);
        String ip = ParamUtils.required(request, "ip");
        String port = ParamUtils.required(request, "port");
        return serviceManager.getInstance(namespace, serviceName, cluster, ip, Integer.parseInt(port));
    }

}
