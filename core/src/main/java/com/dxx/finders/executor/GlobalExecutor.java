package com.dxx.finders.executor;

import java.util.concurrent.ExecutorService;

/**
 * Global executor.
 *
 * @author dxx
 */
public class GlobalExecutor {

    private static final ExecutorService ASYNC_HTTP_REQUEST_EXECUTOR = ExecutorFactory.newFixedExecutorService(
            Runtime.getRuntime().availableProcessors() * 2, new NamedThreadFactory("finders-async-http-request"));

    public static void executeAsyncHttpRequest(Runnable runnable) {
        ASYNC_HTTP_REQUEST_EXECUTOR.execute(runnable);
    }

}
