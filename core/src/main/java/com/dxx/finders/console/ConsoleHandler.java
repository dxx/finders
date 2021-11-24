package com.dxx.finders.console;

import com.dxx.finders.cluster.ServerNodeManager;
import com.dxx.finders.console.vo.ClusterNodeInfo;
import com.dxx.finders.console.vo.InstanceView;
import com.dxx.finders.console.vo.NamespaceInfo;
import com.dxx.finders.console.vo.ServiceView;
import com.dxx.finders.constant.Services;
import com.dxx.finders.core.ServiceManager;
import com.dxx.finders.http.RequestMethod;
import com.dxx.finders.http.annotation.RequestMapping;
import com.dxx.finders.util.JacksonUtils;
import com.dxx.finders.util.ParamUtils;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

import java.util.List;

/**
 * Console handler.
 *
 * @author dxx
 */
public class ConsoleHandler {

    private final ConsoleService consoleService;

    public ConsoleHandler(ServiceManager serviceManager,
                          ServerNodeManager serverNodeManager) {
        this.consoleService = new ConsoleService(serviceManager, serverNodeManager);
    }

    /**
     * Get all name of namespace.
     */
    @RequestMapping(path = "/console/namespace/names", method = RequestMethod.GET)
    public void namespaceNameList(RoutingContext context) {
        HttpServerResponse response = context.response();
        String[] namespaces = consoleService.getNamespaceNames();

        responseJson(response, namespaces);
    }

    /**
     * Get all namespace.
     */
    @RequestMapping(path = "/console/namespaces", method = RequestMethod.GET)
    public void namespaceList(RoutingContext context) {
        HttpServerResponse response = context.response();
        List<NamespaceInfo> namespaceInfoList = consoleService.getNamespaceList();

        responseJson(response, namespaceInfoList);
    }

    /**
     * Get all cluster node.
     */
    @RequestMapping(path = "/console/cluster/nodes", method = RequestMethod.GET)
    public void clusterNodeList(RoutingContext context) {
        HttpServerResponse response = context.response();
        List<ClusterNodeInfo> clusterNodeInfoList = consoleService.getClusterNodeList();

        responseJson(response, clusterNodeInfoList);
    }

    /**
     * Get all service.
     */
    @RequestMapping(path = "/console/services", method = RequestMethod.GET)
    public void serviceList(RoutingContext context) {
        HttpServerRequest request = context.request();
        HttpServerResponse response = context.response();

        String namespace = ParamUtils.optional(request, Services.PARAM_NAMESPACE, Services.DEFAULT_NAMESPACE);
        String serviceName = ParamUtils.optional(request, Services.PARAM_SERVICE_NAME, "");
        String page = ParamUtils.optional(request, "page", "1");
        String size = ParamUtils.optional(request, "size", "10");

        ServiceView serviceView = consoleService.getServiceList(namespace, serviceName,
                Integer.parseInt(page),
                Integer.parseInt(size));

        responseJson(response, serviceView);
    }

    /**
     * Get all instance.
     */
    @RequestMapping(path = "/console/instances", method = RequestMethod.GET)
    public void instanceList(RoutingContext context) {
        HttpServerRequest request = context.request();
        HttpServerResponse response = context.response();

        String namespace = ParamUtils.optional(request, Services.PARAM_NAMESPACE, Services.DEFAULT_NAMESPACE);
        String serviceName = ParamUtils.required(request, Services.PARAM_SERVICE_NAME);

        InstanceView instanceView = consoleService.getInstanceList(namespace, serviceName);

        responseJson(response, instanceView);
    }

    private void responseJson(HttpServerResponse response, Object obj) {
        response.putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON + ";charset=UTF-8");
        response.end(JacksonUtils.toJson(obj));
    }

}
