package com.dxx.finders.console;

import com.dxx.finders.cluster.ServerNode;
import com.dxx.finders.cluster.ServerNodeManager;
import com.dxx.finders.console.vo.ClusterInfo;
import com.dxx.finders.console.vo.NamespaceInfo;
import com.dxx.finders.constant.Services;
import com.dxx.finders.core.ServiceManager;
import com.dxx.finders.http.RequestMethod;
import com.dxx.finders.http.annotation.RequestMapping;
import com.dxx.finders.util.JacksonUtils;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Console handler.
 *
 * @author dxx
 */
public class ConsoleHandler {

    private final ServiceManager serviceManager;

    private final ServerNodeManager serverNodeManager;

    public ConsoleHandler(ServiceManager serviceManager,
                          ServerNodeManager serverNodeManager){
        this.serviceManager = serviceManager;
        this.serverNodeManager = serverNodeManager;
    }

    @RequestMapping(path = "/console/namespace/names", method = RequestMethod.GET)
    public void namespaceNameList(RoutingContext context) {
        HttpServerResponse response = context.response();
        String[] namespaces = serviceManager.getServiceMap().keySet().toArray(new String[]{});
        if (namespaces.length == 0) {
            namespaces = new String[]{Services.DEFAULT_NAMESPACE};
        }
        response.putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON + ";charset=UTF-8");
        response.end(JacksonUtils.toJson(namespaces));
    }

    @RequestMapping(path = "/console/namespaces", method = RequestMethod.GET)
    public void namespacesList(RoutingContext context) {
        HttpServerResponse response = context.response();
        List<NamespaceInfo> namespaceInfoList = new ArrayList<>();

        serviceManager.getServiceMap().forEach((key, val) -> {
            NamespaceInfo namespaceInfo = new NamespaceInfo();
            namespaceInfo.setNamespace(key);
            int serviceCount = 0;

            for (String k : val.keySet()) {
                // If there are no instances, the number is not counted
                if (val.get(k).getAllInstance().isEmpty()) {
                    continue;
                }
                serviceCount++;
            }
            namespaceInfo.setServiceCount(serviceCount);
            namespaceInfoList.add(namespaceInfo);
        });

        response.putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON + ";charset=UTF-8");
        response.end(JacksonUtils.toJson(namespaceInfoList));
    }

    @RequestMapping(path = "/console/clusters", method = RequestMethod.GET)
    public void clusterList(RoutingContext context) {
        HttpServerResponse response = context.response();
        List<ServerNode> serverNodeList = serverNodeManager.allNodes();

        List<ClusterInfo> clusterInfoList = serverNodeList.stream().map(item -> {
            ClusterInfo clusterInfo = new ClusterInfo();
            clusterInfo.setId(item.getId());
            clusterInfo.setIp(item.getIp());
            clusterInfo.setPort(item.getPort());
            clusterInfo.setAddress(item.getAddress());
            clusterInfo.setStatus(item.getStatus().toString());
            return clusterInfo;
        }).collect(Collectors.toList());

        response.putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON + ";charset=UTF-8");
        response.end(JacksonUtils.toJson(clusterInfoList));
    }

}
