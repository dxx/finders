package com.dxx.finders.client.model;

/**
 * Instance status.
 *
 * @author dxx
 */
public enum InstanceStatus {
    HEALTHY, // Health check success
    UN_HEALTHY, // Health check failure
    DISABLE, // Intentionally shutdown
    UNKNOWN
}
