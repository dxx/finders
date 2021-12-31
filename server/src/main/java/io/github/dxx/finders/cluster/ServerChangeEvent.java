package io.github.dxx.finders.cluster;

import io.github.dxx.finders.notify.Event;

/**
 * Server change event.
 *
 * @author dxx
 */
public class ServerChangeEvent implements Event {

    private String id;

    private String address;

    private ServerStatus status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
