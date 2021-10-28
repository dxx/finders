package com.dxx.finders.core;

import com.dxx.finders.cluster.DistributionManager;
import com.dxx.finders.constant.Loggers;
import com.dxx.finders.constant.Services;

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
        if (!DistributionManager.isCluster() || DistributionManager.isResponsible(service.getServiceName())) {
            List<Instance> instances = service.getInstances();
            System.out.println(instances);

            for (Instance instance : instances) {
                if (instance.getStatus() == InstanceStatus.HEALTHY) {
                    if (System.currentTimeMillis() - instance.getLastBeatTimestamp() > Services.INSTANCE_HEARTBEAT_TIMEOUT) {
                        Loggers.EVENT.info("Service {} is unhealthy, health check timeout after {}, last beat: {}",
                                instance.getServiceName(), Services.INSTANCE_HEARTBEAT_TIMEOUT, instance.getLastBeatTimestamp());
                        instance.setStatus(InstanceStatus.UN_HEALTHY);
                    }
                }
                if (System.currentTimeMillis() - instance.getLastBeatTimestamp() > Services.INSTANCE_DELETE_TIMEOUT) {
                    Loggers.EVENT.info("Service {} is valid and will be deleted, last beat: {}",
                            instance.getServiceName(), instance.getLastBeatTimestamp());
                }
            }
        }
    }
}
