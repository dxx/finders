package com.dxx.finders.handler;

import com.dxx.finders.constant.Paths;
import com.dxx.finders.constant.Result;
import com.dxx.finders.constant.Services;
import com.dxx.finders.core.*;
import com.dxx.finders.http.RequestMethod;
import com.dxx.finders.http.annotation.RequestMapping;
import com.dxx.finders.util.JacksonUtils;
import com.dxx.finders.util.ParamUtils;
import com.fasterxml.jackson.databind.JsonNode;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

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
     * Service sync.
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
