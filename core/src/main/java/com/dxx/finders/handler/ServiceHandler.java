package com.dxx.finders.handler;

import com.dxx.finders.constant.Instances;
import com.dxx.finders.constant.Paths;
import com.dxx.finders.constant.Result;
import com.dxx.finders.core.Service;
import com.dxx.finders.core.ServiceManager;
import com.dxx.finders.http.RequestMethod;
import com.dxx.finders.http.annotation.Distribute;
import com.dxx.finders.http.annotation.RequestMapping;
import com.dxx.finders.util.JacksonUtils;
import com.dxx.finders.util.ParamUtils;
import com.dxx.finders.util.StringUtils;
import com.fasterxml.jackson.databind.JsonNode;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

import java.nio.charset.StandardCharsets;

/**
 * Service handler.
 *
 * @author dxx
 */
public class ServiceHandler {

    private final ServiceManager serviceManager;

    public ServiceHandler(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    @RequestMapping(path = Paths.SERVICE_LIST, method = RequestMethod.GET)
    public void list(RoutingContext context) {
        HttpServerRequest request = context.request();
        HttpServerResponse response = context.response();

        String namespace = ParamUtils.optional(request, Instances.PARAM_NAMESPACE, Instances.DEFAULT_NAMESPACE);
        String serviceName = ParamUtils.required(request, Instances.PARAM_SERVICE_NAME);

        Service service = serviceManager.getService(namespace, serviceName);

        response.putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON + ";charset=utf-8");
        response.end(JacksonUtils.toJson(service));
    }

    @Distribute
    @RequestMapping(path = Paths.SERVICE, method = RequestMethod.POST)
    public void register(RoutingContext context) {
        HttpServerResponse response = context.response();

        JsonNode jsonNode = ParamUtils.getBodyAsJsonNode(context);
        String namespace = jsonNode.get(Instances.PARAM_NAMESPACE).asText(Instances.DEFAULT_NAMESPACE);
        String serviceName = jsonNode.get(Instances.PARAM_SERVICE_NAME).asText();

        serviceManager.registerService(namespace, serviceName);

        response.end(Result.SUCCESS);
    }

    @Distribute
    @RequestMapping(path = Paths.SERVICE, method = RequestMethod.DELETE)
    public void deregister(RoutingContext context) {
        HttpServerResponse response = context.response();
        response.end(Result.SUCCESS);
    }

}
