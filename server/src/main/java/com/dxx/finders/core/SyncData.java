package com.dxx.finders.core;

import java.util.List;

/**
 * Synchronized data content.
 *
 * @author dxx
 */
public class SyncData {

    private String namespace;

    private String serviceName;

    private List<Instance> instanceList;

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public List<Instance> getInstanceList() {
        return instanceList;
    }

    public void setInstanceList(List<Instance> instanceList) {
        this.instanceList = instanceList;
    }
}
