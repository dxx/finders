package com.dxx.finders.handler;

import com.dxx.finders.constant.Paths;
import com.dxx.finders.constant.Result;
import com.dxx.finders.core.Service;
import com.dxx.finders.core.ServiceManager;
import com.dxx.finders.core.SyncData;
import com.dxx.finders.http.RequestMethod;
import com.dxx.finders.http.annotation.RequestMapping;
import com.dxx.finders.util.JacksonUtils;
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

    public ServiceHandler(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    @RequestMapping(path = Paths.SERVICE_SYNC, method = RequestMethod.PUT)
    public void sync(RoutingContext context) {
        HttpServerRequest request = context.request();
        HttpServerResponse response = context.response();

        String reqBody = context.getBodyAsString();
        SyncData syncData = JacksonUtils.toObject(reqBody, SyncData.class);
        System.out.println(syncData.getNamespace());
        System.out.println(syncData.getServiceName());
        System.out.println(syncData.getInstanceList());
        String namespace = syncData.getNamespace();
        String serviceName = syncData.getServiceName();
        Service service = serviceManager.getService(namespace, serviceName);
        if (service == null) {
            service = serviceManager.createService(namespace, serviceName);
        }
        serviceManager.syncInstance(service, syncData.getInstanceList());

        response.end(Result.SUCCESS);
    }
}
