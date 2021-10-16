package com.dxx.finders.config;

import java.util.List;

/**
 * The cluster config.
 *
 * @author dxx
 */
public class ClusterConfig {

    private String selfId;

    private List<String> nodes;

    public String getSelfId() {
        return selfId;
    }

    public void setSelfId(String selfId) {
        this.selfId = selfId;
    }

    public List<String> getNodes() {
        return nodes;
    }

    public void setNodes(List<String> nodes) {
        this.nodes = nodes;
    }
}
