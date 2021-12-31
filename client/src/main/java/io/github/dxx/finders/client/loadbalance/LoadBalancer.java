package io.github.dxx.finders.client.loadbalance;

/**
 * Load balancer interface.
 *
 * @author dxx
 */
public interface LoadBalancer {

    String chooseServer();

}
