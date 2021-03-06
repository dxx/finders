package io.github.dxx.finders.client;

import io.github.dxx.finders.client.model.Instance;

import java.util.List;

/**
 * Finders client interface.
 *
 * @author dxx
 */
public interface FindersClientService {

    /**
     * Get all instances of a service.
     *
     * @param serviceName name of service
     * @return A list of instance
     */
    List<Instance> getAllInstances(String serviceName);

    /**
     * Get all instances of a service.
     *
     * @param serviceName name of service
     * @param cluster name of cluster
     * @return A list of instance
     */
    List<Instance> getAllInstances(String serviceName, String cluster);

    /**
     * Get all instances of a service.
     *
     * @param serviceName name of service
     * @param clusters name list of cluster
     * @return A list of instance
     */
    List<Instance> getAllInstances(String serviceName, List<String> clusters);

    /**
     * Get all instances of a service.
     *
     * @param serviceName name of service
     * @param healthyOnly is instance healthyOnly
     * @return A list of instance
     */
    List<Instance> getInstances(String serviceName, boolean healthyOnly);

    /**
     * Get instances of a service.
     *
     * @param serviceName name of service
     * @param cluster name of cluster
     * @param healthyOnly is instance healthyOnly
     * @return A list of instance
     */
    List<Instance> getInstances(String serviceName, String cluster, boolean healthyOnly);

    /**
     * Get instances of a service.
     *
     * @param serviceName name of service
     * @param clusters name list of cluster
     * @param healthyOnly is instance healthyOnly
     * @return A list of instance
     */
    List<Instance> getInstances(String serviceName, List<String> clusters, boolean healthyOnly);

    /**
     * Get instance of a service.
     * @param serviceName name of service
     * @param ip instance ip
     * @param port instance port
     * @return An instance
     */
    Instance getInstance(String serviceName, String ip, int port);

    /**
     * Get instance of a service.
     * @param serviceName name of service
     * @param ip instance ip
     * @param port instance port
     * @param cluster name of cluster
     * @return An instance
     */
    Instance getInstance(String serviceName, String ip, int port, String cluster);

    /**
     * Register an instance of service.
     * @param serviceName name of service
     * @param ip instance ip
     * @param port instance port
     */
    void registerInstance(String serviceName, String ip, int port);

    /**
     * Register an instance of service with specified cluster name.
     * @param serviceName name of service
     * @param ip instance ip
     * @param port instance port
     * @param cluster name of cluster
     */
    void registerInstance(String serviceName, String ip, int port, String cluster);

    /**
     * Register an instance of service with specified instance properties.
     * @param instance instance to register
     */
    void registerInstance(Instance instance);

    /**
     * Deregister an instance of service.
     * @param serviceName name of service
     * @param ip instance ip
     * @param port instance port
     */
    void deregisterInstance(String serviceName, String ip, int port);

    /**
     * Deregister an instance of service.
     * @param serviceName name of service
     * @param ip instance ip
     * @param port instance port
     * @param cluster name of cluster
     */
    void deregisterInstance(String serviceName, String ip, int port, String cluster);

    /**
     * Update an instance of service.
     * @param serviceName name of service
     * @param cluster name of cluster
     * @param instance instance to update
     */
    void updateInstance(String serviceName, String cluster, Instance instance);

    /**
     * Get all service name.
     * @return name of service list
     */
    List<String> getServiceNames();

    /**
     * Server whether health.
     * @return true or false.
     */
    boolean serverHealth();

    /**
     * Close the FindersClientService.
     */
    void close();

}
