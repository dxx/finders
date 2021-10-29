package com.dxx.finders.core;

import com.dxx.finders.constant.Loggers;

import java.util.Collections;
import java.util.List;

/**
 * Handle heartbeat of instance.
 *
 * @author dxx
 */
public class InstanceHeartbeatHandler implements Runnable {

    private final Service service;

    private final Instance instance;

    public InstanceHeartbeatHandler(Service service, Instance instance) {
        this.service = service;
        this.instance = instance;
    }

    @Override
    public void run() {
        List<Instance> instances = service.getInstances(Collections.singletonList(instance.getCluster()));
        String ip = instance.getIp();
        int port = instance.getPort();
        for (Instance instance: instances) {
            if (ip.equals(instance.getIp()) && instance.getPort() == port) {
                instance.setLastBeatTimestamp(System.currentTimeMillis());
                instance.setStatus(InstanceStatus.HEALTHY);
                Loggers.EVENT.info("Service {} is healthy, health check ok", service.getServiceName());
            }
        }
    }
}
