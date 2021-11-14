package com.dxx.finders.client.loadbalance;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Round balancer.
 *
 * @author dxx
 */
public class RoundBalancer implements LoadBalancer {

    private final List<String> serverList;

    private int index;

    public RoundBalancer(List<String> serverList) {
        this.serverList = serverList;
        this.index = -1;
    }

    @Override
    public String chooseServer() {
        synchronized (this) {
            this.index = (this.index + 1) % this.serverList.size();
            return this.serverList.get(this.index);
        }
    }

}
