package com.dxx.finders.core;

import com.dxx.finders.cluster.DistributionManager;
import com.dxx.finders.cluster.ServerNode;
import com.dxx.finders.cluster.ServerNodeManager;
import com.dxx.finders.constant.Loggers;
import com.dxx.finders.constant.Paths;
import com.dxx.finders.constant.Services;
import com.dxx.finders.misc.AsyncHttpCallback;
import com.dxx.finders.misc.FindersHttpClient;
import com.dxx.finders.util.JacksonUtils;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

/**
 * Check and update status of instance.
 *
 * @author dxx
 */
public class InstanceHealthCheckTask implements Runnable {

    private final Service service;

    public InstanceHealthCheckTask(Service service) {
        this.service = service;
    }

    @Override
    public void run() {
        try {
            if (!DistributionManager.isCluster() || DistributionManager.isResponsible(service.getServiceName())) {
                List<Instance> instances = service.getInstances();

                for (Instance instance : instances) {
                    if (instance.getStatus() == InstanceStatus.HEALTHY) {
                        if (System.currentTimeMillis() - instance.getLastBeatTimestamp() > Services.INSTANCE_HEARTBEAT_TIMEOUT) {
                            Loggers.EVENT.info("Service {} is unhealthy, health check timeout after {} ms, last beat: {}",
                                    instance.getServiceName(), Services.INSTANCE_HEARTBEAT_TIMEOUT, instance.getLastBeatTimestamp());
                            instance.setStatus(InstanceStatus.UN_HEALTHY);
                        }
                    }
                    if (System.currentTimeMillis() - instance.getLastBeatTimestamp() > Services.INSTANCE_DELETE_TIMEOUT) {
                        Loggers.EVENT.info("Service {} is invalid and will be deleted, last beat: {}",
                                instance.getServiceName(), instance.getLastBeatTimestamp());
                        deleteInstance(instance);
                    }
                }
            }
        } catch (Exception e) {
            Loggers.EVENT.error("Exception while processing instance health check timeout", e);
        }
    }

    private void deleteInstance(Instance instance) {
        ServerNode serverNode = ServerNodeManager.getLocalNode();
        ObjectNode objectNode = JacksonUtils.createObjectNode();
        objectNode.put("namespace", service.getNamespace());
        objectNode.put("cluster", instance.getCluster());
        objectNode.put("serviceName", instance.getServiceName());
        objectNode.put("ip", instance.getIp());
        objectNode.put("port", instance.getPort());
        FindersHttpClient.asyncDeleteRequest(String.format("http://%s%s", serverNode.getAddress(), Paths.INSTANCE),
                objectNode.toString(), new AsyncHttpCallback<String>() {
            @Override
            public void onSuccess(String s) {
            }

            @Override
            public void onError(Throwable e) {
                Loggers.EVENT.error("Delete instance failed, instance: " + objectNode, e);
            }
        });
    }
}
