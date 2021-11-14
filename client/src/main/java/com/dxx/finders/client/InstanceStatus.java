package com.dxx.finders.client;

/**
 * Instance status.
 *
 * @author dxx
 */
public enum InstanceStatus {
    HEALTHY, // Health check success
    UN_HEALTHY, // Health check failure
    DISABLE, // Intentionally shutdown
    UNKNOWN;

    public static InstanceStatus fromStr(String str) {
        for (InstanceStatus status : InstanceStatus.values()) {
            if (status.name().equalsIgnoreCase(str)) {
                return status;
            }
        }
        return UNKNOWN;
    }
}
