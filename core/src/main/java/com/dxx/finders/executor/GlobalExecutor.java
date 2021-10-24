package com.dxx.finders.executor;

import java.util.concurrent.ExecutorService;

/**
 * Global executor.
 *
 * @author dxx
 */
public class GlobalExecutor {

    private static final ExecutorService SERVICE_UPDATER_EXECUTOR = ExecutorFactory.newSingleExecutorService(
            new NamedThreadFactory("finders-service-updater"));

    public static void executeServiceUpdateTask(Runnable runnable) {
        SERVICE_UPDATER_EXECUTOR.execute(runnable);
    }

}
