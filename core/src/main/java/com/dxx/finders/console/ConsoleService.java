package com.dxx.finders.console;

import com.dxx.finders.cluster.ServerNode;
import com.dxx.finders.cluster.ServerNodeManager;
import com.dxx.finders.console.vo.*;
import com.dxx.finders.constant.Services;
import com.dxx.finders.core.Service;
import com.dxx.finders.core.ServiceManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
                if (filterNoInstance(val.get(k))) {
                    continue;
                }
                serviceCount++;
            }
            namespaceInfo.setServiceCount(serviceCount);
            namespaceInfoList.add(namespaceInfo);
        });

        if (namespaceInfoList.size() == 0) {
            NamespaceInfo namespaceInfo = new NamespaceInfo();
            namespaceInfo.setNamespace(Services.DEFAULT_NAMESPACE);
            namespaceInfo.setServiceCount(0);
            namespaceInfoList.add(namespaceInfo);
        }

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

    public ServiceView getServiceList(String namespace, String serviceName, int page, int size) {
        ServiceView serviceView = new ServiceView();
        serviceView.setPage(page);
        serviceView.setSize(size);
        Map<String, Service> serviceMap = serviceManager.getServiceMap().get(namespace);
        if (serviceMap == null) {
            serviceView.setServiceList(Collections.emptyList());
            return serviceView;
        }
        List<Service> serviceList = new ArrayList<>(serviceMap.values());

        serviceList = serviceList.stream().filter(service ->
                // Filter by the service name
                !filterServiceName(service.getServiceName(), serviceName) &&
                // If there are no instances, the number is not counted
                !filterNoInstance(service)
        ).collect(Collectors.toList());

        List<ServiceInfo> serviceInfoList = serviceList.stream().map(service -> {
            ServiceInfo serviceInfo = new ServiceInfo();
            serviceInfo.setServiceName(service.getServiceName());
            serviceInfo.setClusterCount(service.getClusterCount());
            serviceInfo.setInstanceCount(service.getInstanceCount(false));
            serviceInfo.setHealthyInstanceCount(service.getInstanceCount(true));
            return serviceInfo;
        }).collect(Collectors.toList());
        serviceView.setCount(serviceList.size());
        serviceView.setServiceList(serviceInfoList);
        return serviceView;
    }

    public InstanceView getInstanceList(String namespace, String serviceName) {
        Service service = serviceManager.getService(namespace, serviceName);
        InstanceView instanceView = new InstanceView();
        instanceView.setServiceName(serviceName);
        if (service == null) {
            instanceView.setInstanceList(Collections.emptyList());
            return instanceView;
        }
        List<InstanceInfo> instanceInfoList = service.getAllInstance().stream().map(instance -> {
            InstanceInfo instanceInfo = new InstanceInfo();
            instanceInfo.setInstanceId(instance.getInstanceId());
            instanceInfo.setCluster(instance.getCluster());
            instanceInfo.setServiceName(instance.getServiceName());
            instanceInfo.setIp(instance.getIp());
            instanceInfo.setPort(instance.getPort());
            instanceInfo.setStatus(instance.getStatus().toString());
            return instanceInfo;
        }).collect(Collectors.toList());
        instanceView.setInstanceList(instanceInfoList);
        return instanceView;
    }

    private boolean filterServiceName(String name, String searchName) {
        if (searchName == null || searchName.equals("")) {
            return false;
        }
        String searchRegx = Services.SERVICE_NAME_ANY_PATTERN +
                searchName + Services.SERVICE_NAME_ANY_PATTERN;
        return !name.matches(searchRegx);
    }

    private boolean filterNoInstance(Service service) {
        return service.getAllInstance().isEmpty();
    }

}
