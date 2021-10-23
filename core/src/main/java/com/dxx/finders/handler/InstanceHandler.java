package com.dxx.finders.handler;

import com.dxx.finders.constant.Services;
import com.dxx.finders.constant.Paths;
import com.dxx.finders.constant.Result;
import com.dxx.finders.core.Instance;
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

    @RequestMapping(path = Paths.INSTANCE_LIST, method = RequestMethod.GET)
    public void list(RoutingContext context) {
        HttpServerRequest request = context.request();
        HttpServerResponse response = context.response();

        String namespace = ParamUtils.optional(request, Services.PARAM_NAMESPACE, Services.DEFAULT_NAMESPACE);
        String serviceName = ParamUtils.required(request, Services.PARAM_SERVICE_NAME);
        String clusters = ParamUtils.optional(request, "clusters", "");

        ObjectNode node = doList(namespace, clusters, serviceName);

        response.putHeader(HttpHeaderNames.CONTENT_TYPE,HttpHeaderValues.APPLICATION_JSON + ";charset=utf-8");
        response.end(JacksonUtils.toJson(node));
    }

    @Distribute
    @RequestMapping(path = Paths.INSTANCE, method = RequestMethod.POST)
    public void register(RoutingContext context) {
        HttpServerResponse response = context.response();

        JsonNode jsonNode = ParamUtils.getBodyAsJsonNode(context);
        String namespace = jsonNode.get(Services.PARAM_NAMESPACE).asText(Services.DEFAULT_NAMESPACE);
        String serviceName = jsonNode.get(Services.PARAM_SERVICE_NAME).asText();

        Instance instance = createInstance(jsonNode);

        serviceManager.registerService(namespace, serviceName, instance);

        response.end(Result.SUCCESS);
    }

    @Distribute
    @RequestMapping(path = Paths.INSTANCE, method = RequestMethod.DELETE)
    public void deregister(RoutingContext context) {
        HttpServerResponse response = context.response();
        response.end(Result.SUCCESS);
    }

    private Instance createInstance(JsonNode jsonNode) {
        Instance instance = JacksonUtils.toObject(jsonNode.toString(), Instance.class);
        ParamUtils.requiredCheck(Services.PARAM_SERVICE_NAME, instance.getServiceName());
        ParamUtils.requiredCheck("ip", instance.getIp());
        instance.setClusterName(StringUtils.defaultIfEmpty(instance.getClusterName(), Services.DEFAULT_CLUSTER));

        if (!instance.getServiceName().matches(Services.SERVICE_NAME_SYNTAX)) {
            throw new ValidationException(HttpResponseStatus.BAD_REQUEST.code(),
                    "Service name can only have these characters: 0-9a-zA-Z@.:_-");
        }
        if (!instance.getClusterName().matches(Services.CLUSTER_NAME_SYNTAX)) {
            throw new ValidationException(HttpResponseStatus.BAD_REQUEST.code(),
                    "Cluster name can only have these characters: 0-9a-zA-Z-_");
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

        ObjectNode objectNode = JacksonUtils.createJsonNode();
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
            instances = service.getInstances(clusterNames);
        } else {
            instances = service.getInstances();
            instances.stream().map(Instance::getClusterName).distinct().forEach(clusterArrayNode::add);
        }

        instances.forEach(instanceArrayNode::addPOJO);

        return objectNode;
    }

}