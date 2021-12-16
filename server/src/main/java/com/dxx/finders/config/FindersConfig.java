package com.dxx.finders.config;

/**
 * The config of finders.
 *
 * @author dxx
 */
public class FindersConfig {

    /**
     * Server config has default instance.
     */
    private ServerConfig serverConfig = new ServerConfig();

    private ClusterConfig clusterConfig;

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    public void setServerConfig(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    public ClusterConfig getClusterConfig() {
        return clusterConfig;
    }

    public void setClusterConfig(ClusterConfig clusterConfig) {
        this.clusterConfig = clusterConfig;
    }
}
