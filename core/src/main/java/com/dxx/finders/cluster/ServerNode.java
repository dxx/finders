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

    private ServerStatus status;

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

    public ServerStatus getStatus() {
        return status;
    }

    public void setStatus(ServerStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        ServerNode target = (ServerNode) obj;
        return this.id.equals(target.getId()) &&
                this.ip.equals(target.getIp()) &&
                this.port == target.getPort() &&
                this.status == target.getStatus();
    }

    @Override
    public String toString() {
        return "ServerNode{" +
                "id='" + id + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", address='" + address + '\'' +
                ", status=" + status +
                '}';
    }

    public static class ServerNodeBuilder {

        private String id;

        private String ip;

        private int port;

        private ServerStatus status = ServerStatus.UP;

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

        public ServerNodeBuilder status(ServerStatus status) {
            this.status = status;
            return this;
        }

        public ServerNode build() {
            ServerNode serverNode = new ServerNode();
            serverNode.setId(this.id);
            serverNode.setIp(this.ip);
            serverNode.setPort(this.port);
            serverNode.setAddress(this.ip + ":" + this.port);
            serverNode.setStatus(this.status);
            return serverNode;
        }
    }

}
