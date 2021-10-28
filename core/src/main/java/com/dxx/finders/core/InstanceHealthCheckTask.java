package com.dxx.finders.core;

import com.dxx.finders.cluster.DistributionManager;

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
        }
    }
}
