package com.dxx.finders.notify;

import com.dxx.finders.constant.Loggers;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * The default event publisher implementation.
 *
 * @author dxx
 */
public class DefaultEventPublisher extends Thread implements EventPublisher {

    private volatile boolean initialized = false;

    private volatile boolean shutdown = false;

    private final Class<? extends Event> eventType;

    private final BlockingQueue<Event> eventQueue;

    private final Set<Subscriber<Event>> subscribers = new HashSet<>();

    public DefaultEventPublisher(Class<? extends Event> eventType, int bufferSize) {
        this.eventType = eventType;
        this.eventQueue = new ArrayBlockingQueue<>(bufferSize);
        this.start();
    }

    @Override
    public synchronized void start() {
        if (!initialized) {
            super.start();
            initialized = true;
        }
    }

    @Override
    public void run() {
        this.handleEventQueue();
    }

    private void handleEventQueue() {
        try {
            int waitTimes = 10;
            while (waitTimes > 0) {
                if (shutdown || subscribers.size() > 0) {
                    break;
                }
                TimeUnit.SECONDS.sleep(1);
                waitTimes--;
            }
            while (true) {
                if (shutdown) {
                    break;
                }
                final Event event = this.eventQueue.take();
                receiveEvent(event);
            }
        } catch (InterruptedException e) {
            Loggers.CORE.error("[EventPublisher] An error occurred when handleEventQueue", e);
        }
    }

    private void receiveEvent(Event event) {
        for (Subscriber<Event> subscriber : subscribers) {
            notify(subscriber, event);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addSubscriber(Subscriber subscriber) {
        subscribers.add(subscriber);
    }

    @Override
    public void removeSubscriber(Subscriber subscriber) {
        subscribers.remove(subscriber);
    }

    @Override
    public void publish(Event event) {
        if (!initialized) {
            throw new IllegalStateException("Publisher does not start");
        }
        if (!eventType.isAssignableFrom(event.getClass())) {
            return;
        }
        boolean success = this.eventQueue.offer(event);
        if (!success) {
            receiveEvent(event);
        }
    }

    @Override
    public void notify(Subscriber<Event> subscriber, Event event) {
        subscriber.onEvent(event);
    }

    @Override
    public void shutdown() {
        this.shutdown = true;
        this.eventQueue.clear();
    }

}
