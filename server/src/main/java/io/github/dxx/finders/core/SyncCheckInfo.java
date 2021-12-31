package io.github.dxx.finders.core;

import java.util.HashMap;
import java.util.Map;

/**
 * Synchronized check info.
 *
 * @author dxx
 */
public class SyncCheckInfo {

    private String namespace;

    private Map<String, String> serviceCheckInfo = new HashMap<>();

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public Map<String, String> getServiceCheckInfo() {
        return serviceCheckInfo;
    }

    public void setServiceCheckInfo(Map<String, String> serviceCheckInfo) {
        this.serviceCheckInfo = serviceCheckInfo;
    }
}
