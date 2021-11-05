package com.dxx.finders.core;

import com.dxx.finders.constant.Loggers;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Service update task.
 *
 * @author dxx
 */
public class ServiceUpdater implements Runnable {

    private final BlockingQueue<Pair<Service, List<Instance>>> taskQueue =
            new LinkedBlockingQueue<>(10 * 1024);

    public void addTask(Service service, List<Instance> instances) {
        Pair<Service, List<Instance>> pair = Pair.with(service, instances);
        boolean success = taskQueue.offer(pair);
        if (!success) {
            new Thread(() -> {
                try {
                    taskQueue.put(pair);
                } catch (InterruptedException e) {
                    Loggers.EVENT.error("[Service Updater] Error while put pair into taskQueue", e);
                }
            }).start();
        }
    }

    @Override
    public void run() {
        Loggers.EVENT.info("Service updater started");

        while (true) {
            try {
                Pair<Service, List<Instance>> pair = taskQueue.take();
                pair.getValue0().updateInstance(pair.getValue1());
            } catch (InterruptedException e) {
                Loggers.EVENT.error("[Service Updater] Error while handling instance update task", e);
            }
        }
    }

}
