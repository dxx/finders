package com.dxx.finders.core;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The class that holds instance information.
 *
 * @author dxx
 */
public class Instance {

    private String instanceId;

    private String cluster;

    private String serviceName;

    private String ip;

    private int port;

    private InstanceStatus status = InstanceStatus.HEALTHY;

    @JsonIgnore
    private volatile long lastBeatTimestamp = System.currentTimeMillis();

    public void createInstanceId() {
        this.instanceId = String.format("%s#%s@%s:%s", getCluster(), getServiceName(), getIp(), getPort());
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public InstanceStatus getStatus() {
        return status;
    }

    public void setStatus(InstanceStatus status) {
        this.status = status;
    }

    public long getLastBeatTimestamp() {
        return lastBeatTimestamp;
    }

    public void setLastBeatTimestamp(long lastBeatTimestamp) {
        this.lastBeatTimestamp = lastBeatTimestamp;
    }
}
