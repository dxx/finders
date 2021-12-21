package com.dxx.finders.config;

/**
 * The netty server config.
 *
 * @author dxx
 */
public class ServerConfig {

    private int port = 9080;

    private int backlog = 128;

    private int rcvBufSize = 128 * 1024;

    private int sndBufSize = 128 * 1024;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getBacklog() {
        return backlog;
    }

    public void setBacklog(int backlog) {
        this.backlog = backlog;
    }

    public int getRcvBufSize() {
        return rcvBufSize;
    }

    public void setRcvBufSize(int rcvBufSize) {
        this.rcvBufSize = rcvBufSize;
    }

    public int getSndBufSize() {
        return sndBufSize;
    }

    public void setSndBufSize(int sndBufSize) {
        this.sndBufSize = sndBufSize;
    }
}
