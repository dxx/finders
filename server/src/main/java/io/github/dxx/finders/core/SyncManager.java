package io.github.dxx.finders.core;

import io.github.dxx.finders.cluster.DistributionManager;
import io.github.dxx.finders.cluster.ServerNode;
import io.github.dxx.finders.cluster.ServerNodeManager;
import io.github.dxx.finders.constant.Loggers;
import io.github.dxx.finders.constant.Paths;
import io.github.dxx.finders.constant.Services;
import io.github.dxx.finders.executor.GlobalExecutor;
import io.github.dxx.finders.misc.AsyncHttpCallback;
import io.github.dxx.finders.misc.FindersHttpClient;
import io.github.dxx.finders.util.JacksonUtils;

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

    private ServiceManager serviceManager;

    private ServiceStore serviceStore;

    private final ServiceSynchronizer serviceSynchronizer = new ServiceSynchronizer();

    private final ServiceSyncUpdater serviceSyncUpdater = new ServiceSyncUpdater();

    private final ServiceSyncTask serviceSyncTask = new ServiceSyncTask();

    public SyncManager(ServerNodeManager serverNodeManager) {
        this.serverNodeManager = serverNodeManager;
    }

    public void init(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
        this.serviceStore = serviceManager.getServiceStore();

        GlobalExecutor.executeServiceSync(serviceSynchronizer);
        GlobalExecutor.executeServiceSyncUpdate(serviceSyncUpdater);
        GlobalExecutor.scheduleServiceSyncTask(serviceSyncTask, 30000,
                Services.SERVICE_SYNC_TASK_PERIOD, TimeUnit.MILLISECONDS);
    }

    public void sync(String namespace, String serviceName) {
        List<Instance> instanceList = serviceStore.get(ServiceKey.build(namespace, serviceName));
        SyncData syncData = new SyncData();
        syncData.setNamespace(namespace);
        syncData.setServiceName(serviceName);
        syncData.setInstanceList(instanceList);
        String data = JacksonUtils.toJson(syncData);
        List<ServerNode> serverNodes = serverNodeManager.upNodesWithoutSelf();
        serverNodes.forEach(serverNode -> serviceSynchronizer.addTask(serverNode.getAddress(), data));
    }

    public void verifyCheckInfo(String serverAddress, SyncCheckInfo syncCheckInfo) {
        String namespace = syncCheckInfo.getNamespace();
        syncCheckInfo.getServiceCheckInfo().forEach((serviceName, checkInfo) -> {
            String key = ServiceKey.build(namespace, serviceName);
            if (checkInfo.equals(serviceStore.getCheckInfo(key))) {
                return;
            }
            serviceSyncUpdater.addTask(serverAddress, key);
        });
    }

    public String getServiceData(String namespace, String serviceName) {
        String key = ServiceKey.build(namespace, serviceName);
        List<Instance> instances = serviceStore.get(key);
        SyncData syncData = new SyncData();
        syncData.setNamespace(namespace);
        syncData.setServiceName(serviceName);
        syncData.setInstanceList(instances);
        return JacksonUtils.toJson(syncData);
    }

    private static class ServiceSynchronizer implements Runnable {

        private final int RETRY_COUNT = 1;

        private final BlockingQueue<Pair<String, String>> taskQueue =
                new LinkedBlockingQueue<>(10 * 1024);

        public void addTask(String address, String data) {
            Pair<String, String> pair = Pair.with(address, data);
            boolean success = taskQueue.offer(pair);
            if (!success) {
                GlobalExecutor.executeBackgroundTask(() -> {
                    try {
                        taskQueue.put(pair);
                    } catch (InterruptedException e) {
                        Loggers.EVENT.error("[ServiceSynchronizer] Error while put pair into taskQueue", e);
                    }
                });
            }
        }

        @Override
        public void run() {
            Loggers.EVENT.info("Service synchronizer started");

            while (true) {
                try {
                    Pair<String, String> pair = taskQueue.take();
                    sync(pair.getValue0(), pair.getValue1());
                } catch (InterruptedException e) {
                    Loggers.EVENT.error("[ServiceSynchronizer] Error while handling service sync task", e);
                }
            }
        }

        public void sync(String address, String data) {
            int retry = RETRY_COUNT + 1;
            for (; retry > 0; retry--) {
                try {
                    FindersHttpClient.put(String.format("http://%s%s", address, Paths.SERVICE_SYNC), data);
                    break;
                } catch (Exception e) {
                    Loggers.EVENT.error("[ServiceSynchronizer] Sync service data to {} failed, error: {}, retrying again",
                            address, e.getMessage());
                }
            }
        }
    }

    private class ServiceSyncTask implements Runnable {

        @Override
        public void run() {
            try {
                if (!DistributionManager.isCluster()) {
                    return;
                }
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
                Loggers.EVENT.error("[ServiceSyncTask] Error while handling service sync task", e);
            }
        }

        public void syncCheckInfo(SyncCheckInfo syncCheckInfo) {
            ServerNode localServerNode = serverNodeManager.selfNode();
            List<ServerNode> serverNodes = serverNodeManager.upNodesWithoutSelf();
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
                Loggers.EVENT.error("[ServiceSyncUpdater] Error while put pair into taskQueue", e);
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
                    Loggers.EVENT.error("[ServiceSyncUpdater] Error while handling service sync task", e);
                }
            }
        }

        public void handle(String address, String data) {
            String namespace = ServiceKey.getNamespace(data);
            String serviceName = ServiceKey.getServiceName(data);
            String result = FindersHttpClient.get(String.format("http://%s%s?namespace=%s&serviceName=%s",
                    address, Paths.SERVICE_DATA, namespace, serviceName));
            SyncData syncData = JacksonUtils.toObject(result, SyncData.class);
            Service service = serviceManager.getService(namespace, serviceName);
            if (service == null) {
                service = serviceManager.createService(namespace, serviceName);
            }
            serviceManager.syncInstance(service, syncData.getInstanceList());
        }
    }

}
