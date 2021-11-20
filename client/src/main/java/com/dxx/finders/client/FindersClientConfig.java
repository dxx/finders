package com.dxx.finders.client;

/**
 * The config of finders client.
 *
 * @author dxx
 */
public class FindersClientConfig {

    private final String serverList;

    private final int requestMaxRetry;

    private final int heartbeatThreads;

    private final long heartbeatPeriod;

    private FindersClientConfig(Builder builder) {
        this.serverList = builder.serverList;
        this.requestMaxRetry = builder.requestMaxRetry;
        this.heartbeatThreads = builder.heartbeatThreads;
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

    public int heartbeatThreads() {
        return this.heartbeatThreads;
    }

    public long heartbeatPeriod() {
        return this.heartbeatPeriod;
    }

    public static class Builder {

        private String serverList;

        private int requestMaxRetry = 3;

        private int heartbeatThreads;

        private long heartbeatPeriod = 5000;

        public Builder serverList(String serverList) {
            this.serverList = serverList;
            return this;
        }

        public Builder requestMaxRetry(int requestMaxRetry) {
            this.requestMaxRetry = requestMaxRetry;
            return this;
        }

        public Builder heartbeatThreads(int heartbeatThreads) {
            this.heartbeatThreads = heartbeatThreads;
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
