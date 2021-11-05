package com.dxx.finders.core;

import com.dxx.finders.cluster.DistributionManager;
import com.dxx.finders.cluster.ServerNode;
import com.dxx.finders.cluster.ServerNodeManager;
import com.dxx.finders.constant.Loggers;
import com.dxx.finders.constant.Paths;
import com.dxx.finders.executor.GlobalExecutor;
import com.dxx.finders.misc.AsyncHttpCallback;
import com.dxx.finders.misc.FindersHttpClient;
import com.dxx.finders.util.JacksonUtils;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Data sync management.
 *
 * @author dxx
 */
public class SyncManager {

    private final ServerNodeManager serverNodeManager;

    private ServiceStore serviceStore;

    private final ServiceSynchronizer serviceSynchronizer = new ServiceSynchronizer();

    private final ServiceSyncUpdater serviceSyncUpdater = new ServiceSyncUpdater();

    private final ServiceSyncTask serviceSyncTask = new ServiceSyncTask();

    public SyncManager(ServerNodeManager serverNodeManager) {
        this.serverNodeManager = serverNodeManager;
    }

    public void init(ServiceManager serviceManager) {
        this.serviceStore = serviceManager.getServiceStore();

        GlobalExecutor.executeServiceSync(serviceSynchronizer);
        GlobalExecutor.executeServiceSyncUpdate(serviceSyncUpdater);
        GlobalExecutor.scheduleServiceSyncTask(serviceSyncTask, 30000, 5000, TimeUnit.MILLISECONDS);
    }

    public void sync(String namespace, String serviceName) {
        List<Instance> instanceList = serviceStore.get(ServiceKey.build(namespace, serviceName));
        SyncData syncData = new SyncData();
        syncData.setNamespace(namespace);
        syncData.setServiceName(serviceName);
        syncData.setInstanceList(instanceList);
        String data = JacksonUtils.toJson(syncData);
        List<ServerNode> serverNodes = serverNodeManager.allNodesWithoutSelf();
        serverNodes.forEach(serverNode -> serviceSynchronizer.addTask(serverNode.getAddress(), data));
    }

    public void verifyCheckInfo(String sendAddress, SyncCheckInfo syncCheckInfo) {
        String namespace = syncCheckInfo.getNamespace();
        syncCheckInfo.getServiceCheckInfo().forEach((serviceName, checkInfo) -> {
            String key = ServiceKey.build(namespace, serviceName);
            if (checkInfo.equals(serviceStore.getCheckInfo(key))) {
                return;
            }

        });
    }

    private static class ServiceSynchronizer implements Runnable {

        private final int RETRY_COUNT = 1;

        private final BlockingQueue<Pair<String, String>> taskQueue =
                new LinkedBlockingQueue<>(10 * 1024);

        public void addTask(String address, String data) {
            try {
                taskQueue.put(Pair.with(address, data));
            } catch (InterruptedException e) {
                Loggers.EVENT.error("[Service Synchronizer] Error while put pair into taskQueue", e);
            }
        }

        @Override
        public void run() {
            Loggers.EVENT.info("Service synchronizer started");

            while (true) {
                try {
                    Pair<String, String> pair = taskQueue.take();
                    sync(pair);
                } catch (InterruptedException e) {
                    Loggers.EVENT.error("[Service Synchronizer] Error while handling service sync task", e);
                }
            }
        }

        public void sync(Pair<String, String> pair) {
            int retry = RETRY_COUNT + 1;
            for (; retry > 0; retry--) {
                try {
                    FindersHttpClient.put(String.format("http://%s%s", pair.getValue0(), Paths.SERVICE_SYNC), pair.getValue1());
                    break;
                } catch (Exception e) {
                    Loggers.EVENT.error("[Service Synchronizer] Sync service data to {} failed, error: {}, retrying again",
                            pair.getValue0(), e.getMessage());
                }
            }
        }
    }

    private class ServiceSyncTask implements Runnable {

        @Override
        public void run() {
            try {
                List<String> keys = serviceStore.getKeys();
                Map<String, SyncCheckInfo> syncCheckInfoMap = new HashMap<>();
                for (String key : keys) {
                    String namespace = ServiceKey.getNamespace(key);
                    String serviceName = ServiceKey.getServiceName(key);
                    if (!DistributionManager.isResponsible(serviceName)) {
                        continue;
                    }
                    syncCheckInfoMap.computeIfAbsent(namespace, k -> new SyncCheckInfo());
                    SyncCheckInfo syncCheckInfo = syncCheckInfoMap.get(namespace);
                    syncCheckInfo.setNamespace(namespace);
                    syncCheckInfo.getServiceCheckInfo().put(serviceName, serviceStore.getCheckInfo(key));
                }
                syncCheckInfoMap.values().forEach(this::syncCheckInfo);
            } catch (Exception e) {
                Loggers.EVENT.error("[Service sync Task] Error while handling service sync task", e);
            }
        }

        public void syncCheckInfo(SyncCheckInfo syncCheckInfo) {
            ServerNode localServerNode = serverNodeManager.selfNode();
            List<ServerNode> serverNodes = serverNodeManager.allNodesWithoutSelf();
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("sendAddress", localServerNode.getAddress());
            dataMap.put("checkInfo", syncCheckInfo);
            String data = JacksonUtils.toJson(dataMap);
            serverNodes.forEach(serverNode -> {
                FindersHttpClient.asyncPutRequest(String.format("http://%s%s", serverNode.getAddress(),
                        Paths.SERVICE_VERIFY), data, new AsyncHttpCallback<String>() {
                    @Override
                    public void onSuccess(String s) {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Loggers.EVENT.error("Sync check info failed, current address: {}, remote server address: {}",
                               localServerNode.getAddress() , serverNode.getAddress(), e);
                    }
                });
            });
        }
    }

    private class ServiceSyncUpdater implements Runnable {

        private final BlockingQueue<Pair<String, String>> taskQueue =
                new LinkedBlockingQueue<>(1024);

        public void addTask(String address, String data) {
            try {
                taskQueue.put(Pair.with(address, data));
            } catch (InterruptedException e) {
                Loggers.EVENT.error("[Service sync updater] Error while put pair into taskQueue", e);
            }
        }

        @Override
        public void run() {
            Loggers.EVENT.info("Service sync updater started");

            while (true) {
                try {
                    Pair<String, String> pair = taskQueue.take();
                    handle(pair.getValue0(), pair.getValue1());
                } catch (InterruptedException e) {
                    Loggers.EVENT.error("[Service sync updater] Error while handling service sync task", e);
                }
            }
        }

        public void handle(String address, String data) {

        }
    }

}
