package com.dxx.finders.client.loadbalance;

/**
 * Load balancer interface.
 *
 * @author dxx
 */
public interface LoadBalancer {

    String chooseServer();

}
