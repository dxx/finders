package com.dxx.finders.executor;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Factory of thread executor.
 *
 * @author dxx
 */
public final class ExecutorFactory {

    /**
     * Create a new single executor service with input thread factory.
     */
    public static ExecutorService newSingleExecutorService(NamedThreadFactory threadFactory) {
        return ExecutorFactory.newFixedExecutorService(1, threadFactory);
    }

    /**
     * Create a new fixed executor service with input thread factory.
     */
    public static ExecutorService newFixedExecutorService(int nThreads,
                                                          NamedThreadFactory threadFactory) {
        ExecutorService executorService = Executors.newFixedThreadPool(nThreads, threadFactory);
        ExecutorController.register(executorService);
        return executorService;
    }

    /**
     * Create a new single scheduled executor service with input thread factory.
     */
    public static ScheduledExecutorService newSingleScheduledExecutorService(NamedThreadFactory threadFactory) {
        return ExecutorFactory.newScheduledExecutorService(1, threadFactory);
    }

    /**
     * Create a new scheduled executor service with input thread factory.
     */
    public static ScheduledExecutorService newScheduledExecutorService(int corePoolSize,
                                                                       NamedThreadFactory threadFactory) {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(corePoolSize, threadFactory);
        ExecutorController.register(executorService);
        return executorService;
    }

    /**
     * Control the thread pool resources.
     */
    public static final class ExecutorController {

        private static final int SHUTDOWN_RETRY = 3;

        private static final Set<ExecutorService> EXECUTOR_SERVICES = new HashSet<>();

        static {
            Runtime.getRuntime().addShutdownHook(new Thread(ExecutorController::shutdown));
        }

        public static void register(ExecutorService executor) {
            EXECUTOR_SERVICES.add(executor);
        }

        public static void shutdown() {
            for (ExecutorService executor: EXECUTOR_SERVICES) {
                shutdownThreadPool(executor);
            }
        }

        private static void shutdownThreadPool(ExecutorService executor) {
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

    }
}
