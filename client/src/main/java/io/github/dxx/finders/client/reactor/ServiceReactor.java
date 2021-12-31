package io.github.dxx.finders.client.reactor;

import io.github.dxx.finders.client.FindersClientProxy;
import io.github.dxx.finders.client.model.ServiceInfo;
import io.github.dxx.finders.client.util.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Service reactor.
 *
 * @author dxx
 */
public class ServiceReactor {

    private final Logger LOGGER = LoggerFactory.getLogger(ServiceReactor.class);

    private final Map<String, ScheduledFuture<?>> scheduledFutureMap = new HashMap<>();

    private final Map<String, ServiceInfo> serviceMap = new ConcurrentHashMap<>();

    private final FindersClientProxy clientProxy;

    private final ScheduledExecutorService scheduledExecutor;

    private final long pullPeriod;

    public ServiceReactor(FindersClientProxy clientProxy, int pullTreads, long pullPeriod) {
        if (pullTreads <= 0) {
            pullTreads = ThreadUtils.DEFAULT_SERVICE_POLL_THREAD;
        }
        this.clientProxy = clientProxy;
        this.pullPeriod = pullPeriod;
        this.scheduledExecutor = Executors.newScheduledThreadPool(pullTreads,
                ThreadUtils.newNamedThreadFactory("service-update-task"));
    }

    public ServiceInfo getService(String serviceName, List<String> clusters) {
        String key = serviceKey(serviceName, clusters);
        synchronized (key.intern()) {
            if (!serviceMap.containsKey(key)) {
                updateService(serviceName, clusters);
            }
        }
        scheduleUpdateIfAbsent(serviceName, clusters);
        return serviceMap.get(key);
    }

    private void scheduleUpdateIfAbsent(String serviceName, List<String> clusters) {
        String key = serviceKey(serviceName, clusters);
        if (scheduledFutureMap.containsKey(key)) {
            return;
        }
        ScheduledFuture<?> scheduledFuture = scheduledExecutor.schedule(new ServiceUpdateTask(serviceName, clusters),
                pullPeriod, TimeUnit.MILLISECONDS);
        scheduledFutureMap.put(key, scheduledFuture);
    }

    private void updateService(String serviceName, List<String> clusters) {
        ServiceInfo serviceInfo = clientProxy.getService(serviceName, clusters);
        serviceMap.put(serviceKey(serviceName, clusters), serviceInfo);
    }

    public void shutdown() {
        this.scheduledFutureMap.forEach((key, val) -> val.cancel(true));
        this.scheduledFutureMap.clear();
        ThreadUtils.shutdownThreadPool(scheduledExecutor);
    }

    private String serviceKey(String serviceName, List<String> clusters) {
        return String.format("%s#%s", String.join(",", clusters.toArray(new String[]{})), serviceName);
    }

    private class ServiceUpdateTask implements Runnable {

        private final String serviceName;

        private final List<String> clusters;

        public ServiceUpdateTask(String serviceName, List<String> clusters) {
            this.serviceName = serviceName;
            this.clusters = clusters;
        }

        @Override
        public void run() {
            long start = System.currentTimeMillis();
            long delayTime = pullPeriod;
            try {
                updateService(serviceName, clusters);
                delayTime = delayTime - (System.currentTimeMillis() - start);
            } catch (Exception e) {
                LOGGER.error("Service update error: ", e);
            } finally {
                scheduledExecutor.schedule(this, delayTime, TimeUnit.MILLISECONDS);
            }
        }

    }
}
