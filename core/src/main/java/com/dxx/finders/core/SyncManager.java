package com.dxx.finders.core;

import com.dxx.finders.cluster.ServerNode;
import com.dxx.finders.cluster.ServerNodeManager;
import com.dxx.finders.constant.Loggers;
import com.dxx.finders.constant.Paths;
import com.dxx.finders.executor.GlobalExecutor;
import com.dxx.finders.misc.FindersHttpClient;
import com.dxx.finders.util.JacksonUtils;
import io.vertx.core.http.HttpMethod;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Data sync management.
 *
 * @author dxx
 */
public class SyncManager {

    private final ServerNodeManager serverNodeManager;

    private ServiceManager serviceManager;

    private ServiceStore serviceStore;

    private final ServiceSynchronizer serviceSynchronizer = new ServiceSynchronizer();

    public SyncManager(ServerNodeManager serverNodeManager) {
        this.serverNodeManager = serverNodeManager;
    }

    public void init(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
        this.serviceStore = serviceManager.getServiceStore();

        GlobalExecutor.executeServiceSync(serviceSynchronizer);
    }

    public void sync(String namespace, String serviceName) {
        List<Instance> instanceList = serviceStore.get(namespace, serviceName);
        SyncData syncData = new SyncData();
        syncData.setNamespace(namespace);
        syncData.setServiceName(serviceName);
        syncData.setInstanceList(instanceList);
        String data = JacksonUtils.toJson(syncData);
        List<ServerNode> serverNodes = serverNodeManager.allNodesWithoutSelf();
        serverNodes.forEach(serverNode -> serviceSynchronizer.addTask(serverNode.getAddress(), data));
    }

    public static class ServiceSynchronizer implements Runnable {

        private final int RETRY_COUNT = 1;

        private final BlockingQueue<Pair<String, String>> taskQueue =
                new LinkedBlockingQueue<>(10 * 1024);

        public void addTask(String address, String data) {
            try {
                taskQueue.put(Pair.with(address, data));
            } catch (InterruptedException e) {
                Loggers.EVENT.error("[Service Sync Task] Error while put pair into taskQueue", e);
            }
        }

        @Override
        public void run() {
            Loggers.EVENT.info("Service sync task started");

            while (true) {
                try {
                    Pair<String, String> pair = taskQueue.take();
                    sync(pair);
                } catch (InterruptedException e) {
                    Loggers.EVENT.error("[Service Sync Task] Error while handling service sync task", e);
                }
            }
        }

        public void sync(Pair<String, String> pair) {
            int retry = RETRY_COUNT + 1;
            for (; retry > 0; retry--) {
                try {
                    FindersHttpClient.request(String.format("http://%s%s", pair.getValue0(), Paths.SERVICE_SYNC),
                            HttpMethod.PUT, pair.getValue1());
                    break;
                } catch (Exception e) {
                    Loggers.EVENT.error("[Service Sync Task] Sync service data to {} failed, error: {}, retrying again",
                            pair.getValue0(), e.getMessage());
                }
            }
        }
    }
}
