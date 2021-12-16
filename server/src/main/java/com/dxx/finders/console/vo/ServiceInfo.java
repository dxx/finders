package com.dxx.finders.console.vo;

/**
 * Service info.
 *
 * @author dxx
 */
public class ServiceInfo {

    private String serviceName;

    private int clusterCount;

    private int instanceCount;

    private int healthyInstanceCount;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public int getClusterCount() {
        return clusterCount;
    }

    public void setClusterCount(int clusterCount) {
        this.clusterCount = clusterCount;
    }

    public int getInstanceCount() {
        return instanceCount;
    }

    public void setInstanceCount(int instanceCount) {
        this.instanceCount = instanceCount;
    }

    public int getHealthyInstanceCount() {
        return healthyInstanceCount;
    }

    public void setHealthyInstanceCount(int healthyInstanceCount) {
        this.healthyInstanceCount = healthyInstanceCount;
    }
}
