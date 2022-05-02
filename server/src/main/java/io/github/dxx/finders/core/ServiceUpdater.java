package io.github.dxx.finders.core;

import io.github.dxx.finders.constant.Loggers;
import io.github.dxx.finders.executor.GlobalExecutor;

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
            GlobalExecutor.executeBackgroundTask(() -> {
                try {
                    taskQueue.put(pair);
                } catch (InterruptedException e) {
                    Loggers.EVENT.error("[ServiceUpdater] Error while put pair into taskQueue", e);
                }
            });
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
                Loggers.EVENT.error("[ServiceUpdater] Error while handling instance update task", e);
            }
        }
    }

}
