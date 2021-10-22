package com.dxx.finders.executor;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Factory of thread with name.
 *
 * @author dxx
 */
public class NamedThreadFactory implements ThreadFactory {

    private final AtomicInteger id = new AtomicInteger(0);

    private final String name;

    public NamedThreadFactory(String name) {
        this.name = name;
    }

    @Override
    public Thread newThread(Runnable runnable) {
        String threadName = name + "-" + id.getAndDecrement();
        Thread thread = new Thread(runnable, threadName);
        thread.setDaemon(true);
        return thread;
    }

}
