package io.github.dxx.finders.client.reactor;

import io.github.dxx.finders.client.FindersClientProxy;
import io.github.dxx.finders.client.model.Heartbeat;
import io.github.dxx.finders.client.util.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Heartbeat reactor.
 *
 * @author dxx
 */
public class HeartbeatReactor {

    private final Logger LOGGER = LoggerFactory.getLogger(HeartbeatReactor.class);

    private final Map<String, Heartbeat> heartbeatMap = new HashMap<>();

    private final FindersClientProxy clientProxy;

    private final ScheduledExecutorService scheduledExecutor;

    public HeartbeatReactor(FindersClientProxy clientProxy, int heartbeatThreads) {
        if (heartbeatThreads <= 0) {
            heartbeatThreads = ThreadUtils.DEFAULT_HEARTBEAT_THREAD;
        }
        this.clientProxy = clientProxy;
        this.scheduledExecutor = Executors.newScheduledThreadPool(heartbeatThreads,
                ThreadUtils.newNamedThreadFactory("heartbeat-task"));
    }

    public void addHeartbeat(Heartbeat heartbeat) {
        String key = heartbeatKey(heartbeat.getCluster(), heartbeat.getServiceName(), heartbeat.getIp(),
                heartbeat.getPort());
        this.removeHeartbeat(heartbeat.getCluster(), heartbeat.getServiceName(), heartbeat.getIp(),
                heartbeat.getPort());
        this.scheduledExecutor.schedule(new HeartbeatTask(heartbeat), 0, TimeUnit.MILLISECONDS);
        this.heartbeatMap.put(key, heartbeat);
    }

    public void removeHeartbeat(String cluster, String serviceName, String ip, int port) {
        Heartbeat heartbeat = this.heartbeatMap.remove(heartbeatKey(cluster, serviceName, ip, port));
        if (heartbeat == null) {
            return;
        }
        heartbeat.setStopped(true);
    }

    public void shutdown() {
        this.heartbeatMap.forEach((key, val) -> val.setStopped(true));
        this.heartbeatMap.clear();
        ThreadUtils.shutdownThreadPool(this.scheduledExecutor);
    }

    private String heartbeatKey(String cluster, String serviceName, String ip, int port) {
        return String.format("%s#%s@%s:%s", cluster, serviceName, ip, port);
    }

    private class HeartbeatTask implements Runnable {

        private final Heartbeat heartbeat;

        public HeartbeatTask(Heartbeat heartbeat) {
            this.heartbeat = heartbeat;
        }

        @Override
        public void run() {
            if (heartbeat.isStopped()) {
                return;
            }
            long start = System.currentTimeMillis();
            long delayTime = heartbeat.getPeriod();
            try {
                clientProxy.sendHeartbeat(heartbeat);
                delayTime = delayTime - (System.currentTimeMillis() - start);
            } catch (Exception e) {
                LOGGER.error("Send heartbeat error: ", e);
            } finally {
                scheduledExecutor.schedule(this, delayTime, TimeUnit.MILLISECONDS);
            }
        }

    }
}
