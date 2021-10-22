package com.dxx.finders.config;

/**
 * The netty server config.
 *
 * @author dxx
 */
public class ServerConfig {

    private int port = 9080;

    private int backlog = 128;

    private int rcvBuf = 128 * 1024;

    private int sndBuf = 128 * 1024;

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

    public int getRcvBuf() {
        return rcvBuf;
    }

    public void setRcvBuf(int rcvBuf) {
        this.rcvBuf = rcvBuf;
    }

    public int getSndBuf() {
        return sndBuf;
    }

    public void setSndBuf(int sndBuf) {
        this.sndBuf = sndBuf;
    }
}
