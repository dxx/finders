package com.dxx.finders.cluster;

/**
 * Cluster node.
 *
 * @author dxx
 */
public class ServerNode {

    private String id;

    private String ip;

    private int port;

    private String address;

    public static ServerNodeBuilder builder() {
        return new ServerNodeBuilder();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "ServerNode{" +
                "id='" + id + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", address='" + address + '\'' +
                '}';
    }

    public static class ServerNodeBuilder {

        private String id;

        private String ip;

        private int port;

        private ServerNodeBuilder() {}

        public ServerNodeBuilder id(String id) {
            this.id = id;
            return this;
        }

        public ServerNodeBuilder ip(String ip) {
            this.ip = ip;
            return this;
        }

        public ServerNodeBuilder port(int port) {
            this.port = port;
            return this;
        }

        public ServerNode build() {
            ServerNode serverNode = new ServerNode();
            serverNode.setId(this.id);
            serverNode.setIp(this.ip);
            serverNode.setPort(this.port);
            serverNode.setAddress(this.ip + ":" + this.port);
            return serverNode;
        }
    }

}
