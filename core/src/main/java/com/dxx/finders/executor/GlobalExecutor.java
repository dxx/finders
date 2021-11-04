package com.dxx.finders.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Global executor.
 *
 * @author dxx
 */
public class GlobalExecutor {

    private static final int DEFAULT_THREAD_COUNT = Runtime.getRuntime().availableProcessors() / 2;

    private static final ExecutorService SERVICE_UPDATER_EXECUTOR = ExecutorFactory.newSingleExecutorService(
            new NamedThreadFactory("finders-service-updater"));

    private static final ScheduledExecutorService SERVICE_HEALTHY_CHECK_EXECUTOR = ExecutorFactory.
            newScheduledExecutorService(DEFAULT_THREAD_COUNT, new NamedThreadFactory("finders-service-health-checker"));

    private static final ScheduledExecutorService SERVICE_SYNC_EXECUTOR = ExecutorFactory.
            newScheduledExecutorService(DEFAULT_THREAD_COUNT, new NamedThreadFactory("finders-service-synchronizer"));

    public static void executeServiceUpdateTask(Runnable runnable) {
        SERVICE_UPDATER_EXECUTOR.execute(runnable);
    }

    public static void scheduleHeartbeatHandler(Runnable runnable, long delay, TimeUnit unit) {
        SERVICE_HEALTHY_CHECK_EXECUTOR.schedule(runnable, delay, unit);
    }

    public static void scheduleHealthCheckTask(Runnable runnable, long initialDelay, long delay,
                                               TimeUnit unit) {
        SERVICE_HEALTHY_CHECK_EXECUTOR.scheduleWithFixedDelay(runnable, initialDelay, delay, unit);
    }

    public static void executeServiceSyncTask(Runnable runnable) {
        SERVICE_SYNC_EXECUTOR.execute(runnable);
    }
}
