package com.dxx.finders.console;

import com.dxx.finders.cluster.ServerNode;
import com.dxx.finders.cluster.ServerNodeManager;
import com.dxx.finders.console.vo.ClusterNodeInfo;
import com.dxx.finders.console.vo.NamespaceInfo;
import com.dxx.finders.constant.Services;
import com.dxx.finders.core.ServiceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Console service.
 *
 * @author dxx
 */
public class ConsoleService {

    private final ServiceManager serviceManager;

    private final ServerNodeManager serverNodeManager;

    public ConsoleService(ServiceManager serviceManager,
                          ServerNodeManager serverNodeManager){
        this.serviceManager = serviceManager;
        this.serverNodeManager = serverNodeManager;
    }

    public String[] getNamespaceNames() {
        String[] namespaces = serviceManager.getServiceMap().keySet().toArray(new String[]{});
        if (namespaces.length == 0) {
            namespaces = new String[]{Services.DEFAULT_NAMESPACE};
        }
        return namespaces;
    }

    public List<NamespaceInfo> getNamespaceList() {
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

        return namespaceInfoList;
    }

    public List<ClusterNodeInfo> getClusterNodeList() {
        List<ServerNode> serverNodeList = serverNodeManager.allNodes();

        return serverNodeList.stream().map(item -> {
            ClusterNodeInfo clusterNodeInfo = new ClusterNodeInfo();
            clusterNodeInfo.setId(item.getId());
            clusterNodeInfo.setIp(item.getIp());
            clusterNodeInfo.setPort(item.getPort());
            clusterNodeInfo.setAddress(item.getAddress());
            clusterNodeInfo.setStatus(item.getStatus().toString());
            return clusterNodeInfo;
        }).collect(Collectors.toList());
    }

}
