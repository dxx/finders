package io.github.dxx.finders.handler;

import io.github.dxx.finders.constant.Paths;
import io.github.dxx.finders.constant.Result;
import io.github.dxx.finders.constant.Services;
import io.github.dxx.finders.core.*;
import io.github.dxx.finders.http.RequestMethod;
import io.github.dxx.finders.http.annotation.RequestMapping;
import io.github.dxx.finders.util.JacksonUtils;
import io.github.dxx.finders.util.ParamUtils;
import com.fasterxml.jackson.databind.JsonNode;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Service handler.
 *
 * @author dxx
 */
public class ServiceHandler {

    private final ServiceManager serviceManager;

    private final SyncManager syncManager;

    public ServiceHandler(ServiceManager serviceManager, SyncManager syncManager) {
        this.serviceManager = serviceManager;
        this.syncManager = syncManager;
    }

    /**
     * Get all service names.
     */
    @RequestMapping(path = Paths.SERVICE_NAMES, method = RequestMethod.GET)
    public void names(RoutingContext context) {
        HttpServerRequest request = context.request();
        HttpServerResponse response = context.response();

        String namespace = ParamUtils.optional(request, Services.PARAM_NAMESPACE, Services.DEFAULT_NAMESPACE);
        Map<String, Service> serviceMap = serviceManager.getServiceMap().get(namespace);
        List<String> names = new ArrayList<>();
        if (serviceMap == null) {
            response.end(JacksonUtils.toJson(names));
            return;
        }
        response.end(JacksonUtils.toJson(new ArrayList<>(serviceMap.keySet())));
    }

    /**
     * Service data sync.
     */
    @RequestMapping(path = Paths.SERVICE_SYNC, method = RequestMethod.PUT)
    public void sync(RoutingContext context) {
        HttpServerResponse response = context.response();

        String reqBody = context.getBodyAsString();
        SyncData syncData = JacksonUtils.toObject(reqBody, SyncData.class);
        String namespace = syncData.getNamespace();
        String serviceName = syncData.getServiceName();
        Service service = serviceManager.getService(namespace, serviceName);
        if (service == null) {
            service = serviceManager.createService(namespace, serviceName);
        }
        serviceManager.syncInstance(service, syncData.getInstanceList());

        response.end(Result.SUCCESS);
    }

    /**
     * Verify service info.
     */
    @RequestMapping(path = Paths.SERVICE_VERIFY, method = RequestMethod.PUT)
    public void verify(RoutingContext context) {
        HttpServerResponse response = context.response();

        JsonNode jsonNode = ParamUtils.getBodyAsJsonNode(context);
        String sendAddress = jsonNode.get("sendAddress").asText();
        String checkInfo = jsonNode.get("checkInfo").toString();
        SyncCheckInfo syncCheckInfo = JacksonUtils.toObject(checkInfo, SyncCheckInfo.class);
        syncManager.verifyCheckInfo(sendAddress, syncCheckInfo);

        response.end(Result.SUCCESS);
    }

    /**
     * Get data of service.
     */
    @RequestMapping(path = Paths.SERVICE_DATA, method = RequestMethod.GET)
    public void data(RoutingContext context) {
        HttpServerResponse response = context.response();
        HttpServerRequest request = context.request();

        String namespace = ParamUtils.required(request, Services.PARAM_NAMESPACE);
        String serviceName = ParamUtils.required(request, Services.PARAM_SERVICE_NAME);
        String data = syncManager.getServiceData(namespace, serviceName);

        response.end(data);
    }
}
