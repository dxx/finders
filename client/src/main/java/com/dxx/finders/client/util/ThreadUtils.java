package com.dxx.finders.client.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread utils.
 *
 * @author dxx
 */
public class ThreadUtils {

    private static final int SHUTDOWN_RETRY = 3;

    public static final int DEFAULT_SERVICE_POLL_THREAD = Runtime.getRuntime().availableProcessors() / 2;

    public static final int DEFAULT_HEARTBEAT_THREAD = Runtime.getRuntime().availableProcessors() / 2;

    public static ThreadFactory newNamedThreadFactory(String name) {
        return new NamedThreadFactory(name);
    }

    public static void shutdownThreadPool(ExecutorService executor) {
        executor.shutdown();
        int retry = SHUTDOWN_RETRY;
        while (retry > 0) {
            try {
                if (executor.awaitTermination(1, TimeUnit.SECONDS)) {
                    return;
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
            retry--;
        }
    }

    static class NamedThreadFactory implements ThreadFactory {

        private final AtomicInteger id = new AtomicInteger(0);

        private final String name;

        public NamedThreadFactory(String name) {
            this.name = name;
        }

        @Override
        public Thread newThread(Runnable runnable) {
            String threadName = name + "-" + id.getAndIncrement();
            Thread thread = new Thread(runnable, threadName);
            thread.setDaemon(true);
            return thread;
        }

    }

}
