package com.dxx.finders.cluster;

import com.dxx.finders.constant.Loggers;
import com.dxx.finders.constant.Paths;
import com.dxx.finders.constant.Servers;
import com.dxx.finders.executor.GlobalExecutor;
import com.dxx.finders.misc.AsyncHttpCallback;
import com.dxx.finders.misc.FindersHttpClient;
import com.dxx.finders.notify.Notifier;
import com.dxx.finders.util.StringUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Server status management.
 *
 * @author dxx
 */
public class ServerStatusManager {

    private final ServerNodeManager serverNodeManager;

    private final ServerUpdaterTask serverUpdaterTask = new ServerUpdaterTask();

    public ServerStatusManager(ServerNodeManager serverNodeManager) {
        this.serverNodeManager = serverNodeManager;

        init();
    }

    private void init() {
        GlobalExecutor.scheduleServerUpdateTask(serverUpdaterTask, 0,
                Servers.SERVER_UPDATE_TASK_PERIOD, TimeUnit.MILLISECONDS);
    }

    private class ServerUpdaterTask implements Runnable {

        @Override
        public void run() {
            ServerNode localServerNode = serverNodeManager.selfNode();
            List<ServerNode> allNodes = serverNodeManager.allNodes();
            for (ServerNode serverNode : allNodes) {
                if (localServerNode.getAddress().equals(serverNode.getAddress())) {
                    continue;
                }
                updateServer(serverNode);
            }
        }

        public void updateServer(ServerNode serverNode) {
            FindersHttpClient.asyncGetRequest(String.format("http://%s%s", serverNode.getAddress(),
                    Paths.SERVER_HEALTH),new AsyncHttpCallback<String>() {

                @Override
                public void onSuccess(String result) {
                    if (StringUtils.isNotEmpty(result)) {
                        updateServer(serverNode, ServerStatus.UP);
                        return;
                    }
                    updateServer(serverNode, ServerStatus.DOWN);
                }

                @Override
                public void onError(Throwable e) {
                    Loggers.EVENT.warn("[ServerUpdaterTask] Check server {} health failed", serverNode.getAddress());
                    updateServer(serverNode, ServerStatus.DOWN);
                }
            });
        }

        public void updateServer(ServerNode serverNode, ServerStatus status) {
            if (serverNode.getStatus() != status) {
                ServerChangeEvent serverChangeEvent = new ServerChangeEvent();
                serverChangeEvent.setId(serverNode.getId());
                serverChangeEvent.setAddress(serverNode.getAddress());
                serverChangeEvent.setStatus(status);

                Notifier.publishEvent(serverChangeEvent);
            }
        }

    }
}
