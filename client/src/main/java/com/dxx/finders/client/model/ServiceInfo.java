package com.dxx.finders.client.model;

import java.util.List;

/**
 * Service info.
 *
 * @author dxx
 */
public class ServiceInfo {

    private String serviceName;

    private List<String> clusters;

    private List<Instance> instances;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public List<String> getClusters() {
        return clusters;
    }

    public void setClusters(List<String> clusters) {
        this.clusters = clusters;
    }

    public List<Instance> getInstances() {
        return instances;
    }

    public void setInstances(List<Instance> instances) {
        this.instances = instances;
    }

}
