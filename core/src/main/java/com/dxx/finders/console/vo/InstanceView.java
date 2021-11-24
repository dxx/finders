package com.dxx.finders.console.vo;

import java.util.List;

/**
 * Instance view.
 *
 * @author dxx
 */
public class InstanceView {

    private String serviceName;

    private List<InstanceInfo> instanceList;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public List<InstanceInfo> getInstanceList() {
        return instanceList;
    }

    public void setInstanceList(List<InstanceInfo> instanceList) {
        this.instanceList = instanceList;
    }
}
