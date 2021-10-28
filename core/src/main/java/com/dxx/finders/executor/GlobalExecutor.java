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

    private static final ScheduledExecutorService SERVICE_HEALTHY_CHECK_EXECUTOR = ExecutorFactory.newScheduledExecutorService(DEFAULT_THREAD_COUNT,
            new NamedThreadFactory("finders-service-health-checker"));

    public static void executeServiceUpdateTask(Runnable runnable) {
        SERVICE_UPDATER_EXECUTOR.execute(runnable);
    }

    public static void scheduleServiceHealthCheckTask(Runnable runnable, long initialDelay, long delay,
                                                      TimeUnit unit) {
        SERVICE_HEALTHY_CHECK_EXECUTOR.scheduleWithFixedDelay(runnable, initialDelay, delay, unit);
    }

}
