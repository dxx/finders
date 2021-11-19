package com.dxx.finders.client.reactor;

import com.dxx.finders.client.FindersClientProxy;
import com.dxx.finders.client.model.Heartbeat;

import java.util.HashMap;
import java.util.Map;

/**
 * Heartbeat reactor.
 *
 * @author dxx
 */
public class HeartbeatReactor {

    private final FindersClientProxy clientProxy;

    private Map<String, Heartbeat> heartbeatMap = new HashMap<>();

    public HeartbeatReactor(FindersClientProxy clientProxy) {
        this.clientProxy = clientProxy;
    }

    private class HeartbeatTask implements Runnable {

        private Heartbeat heartbeat;

        public HeartbeatTask(Heartbeat heartbeat) {
            this.heartbeat = heartbeat;
        }

        @Override
        public void run() {

        }

    }
}
