package com.dxx.finders.client;

/**
 * The config of finders client.
 *
 * @author dxx
 */
public class FindersClientConfig {

    /**
     * Url list of finders server.
     */
    private final String serverList;

    /**
     * Number of max retries when the request fails.
     */
    private final int requestMaxRetry;

    /**
     * Number of threads processing the pull service.
     */
    private final int servicePullThreads;

    /**
     * Number of threads processing the heartbeat.
     */
    private final int heartbeatThreads;

    /**
     * Cycle of the service pull.
     */
    private final long servicePullPeriod;

    /**
     * Cycle of the heartbeat.
     */
    private final long heartbeatPeriod;

    private FindersClientConfig(Builder builder) {
        this.serverList = builder.serverList;
        this.requestMaxRetry = builder.requestMaxRetry;
        this.servicePullThreads = builder.servicePullThreads;
        this.heartbeatThreads = builder.heartbeatThreads;
        this.servicePullPeriod = builder.servicePullPeriod;
        this.heartbeatPeriod = builder.heartbeatPeriod;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String serverList() {
        return this.serverList;
    }

    public int requestMaxRetry() {
        return this.requestMaxRetry;
    }

    public int servicePullThreads() {
        return this.servicePullThreads;
    }

    public int heartbeatThreads() {
        return this.heartbeatThreads;
    }

    public long servicePullPeriod() {
        return this.servicePullPeriod;
    }

    public long heartbeatPeriod() {
        return this.heartbeatPeriod;
    }

    public static class Builder {

        private String serverList;

        private int requestMaxRetry = 3;

        private int servicePullThreads;

        private int heartbeatThreads;

        private long servicePullPeriod = 60000;

        private long heartbeatPeriod = 5000;

        public Builder serverList(String serverList) {
            this.serverList = serverList;
            return this;
        }

        public Builder requestMaxRetry(int requestMaxRetry) {
            this.requestMaxRetry = requestMaxRetry;
            return this;
        }

        public Builder servicePullThreads(int servicePullThreads) {
            this.servicePullThreads = servicePullThreads;
            return this;
        }

        public Builder heartbeatThreads(int heartbeatThreads) {
            this.heartbeatThreads = heartbeatThreads;
            return this;
        }

        public Builder servicePullPeriod(int servicePullPeriod) {
            this.servicePullPeriod = servicePullPeriod;
            return this;
        }

        public Builder heartbeatPeriod(int heartbeatPeriod) {
            this.heartbeatPeriod = heartbeatPeriod;
            return this;
        }

        public FindersClientConfig build() {
            return new FindersClientConfig(this);
        }

    }
}
