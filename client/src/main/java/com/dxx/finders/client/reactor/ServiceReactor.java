package com.dxx.finders.client.reactor;

import com.dxx.finders.client.FindersClientProxy;
import com.dxx.finders.client.model.ServiceInfo;
import com.dxx.finders.client.util.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

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

    public ServiceReactor(FindersClientProxy clientProxy, int pullTreads) {
        if (pullTreads <= 0) {
            pullTreads = ThreadUtils.DEFAULT_SERVICE_POLL_THREAD;
        }
        this.clientProxy = clientProxy;
        this.scheduledExecutor = Executors.newScheduledThreadPool(pullTreads,
                ThreadUtils.newNamedThreadFactory("service-update-task"));
    }

    public ServiceInfo getService(String serviceName, List<String> clusters) {
        String key = serviceKey(serviceName, clusters);
        if (!serviceMap.containsKey(key)) {
            updateService(serviceName, clusters);
        }
        return serviceMap.get(key);
    }

    private void updateService(String serviceName, List<String> clusters) {
        ServiceInfo serviceInfo = clientProxy.getService(serviceName, clusters);
        serviceMap.put(serviceKey(serviceName, clusters), serviceInfo);
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
            try {

            } catch (Exception e) {
                LOGGER.error("Service update error: ", e);
            } finally {

            }
        }

    }
}
