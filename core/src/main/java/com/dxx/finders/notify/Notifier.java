package com.dxx.finders.notify;

import com.dxx.finders.constant.Loggers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Notifier of event publish.
 *
 * @author dxx
 */
public final class Notifier {

    public static int BUFFER_SIZE = 1024;

    private static final Map<String, EventPublisher> PUBLISHER_MAP = new ConcurrentHashMap<>(16);

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(Notifier::shutdown));
    }

    /**
     * Register subscriber.
     *
     * @param subscriber subscriber instance.
     */
    @SuppressWarnings("unchecked")
    public static void registerSubscriber(Subscriber subscriber) {
        String topic = getTopic(subscriber.subscribeType());
        if (PUBLISHER_MAP.get(topic) == null) {
            synchronized (Notifier.class) {
                putIfAbsent(topic, subscriber.subscribeType());
            }
        }
        PUBLISHER_MAP.get(topic).addSubscriber(subscriber);
    }

    /**
     * Deregister subscriber.
     *
     * @param subscriber subscriber instance.
     */
    @SuppressWarnings("unchecked")
    public static void deregisterSubscriber(Subscriber subscriber) {
        String topic = getTopic(subscriber.subscribeType());
        EventPublisher eventPublisher = PUBLISHER_MAP.get(topic);
        if (eventPublisher != null) {
            eventPublisher.removeSubscriber(subscriber);
        }
    }

    public static void publishEvent(Event event) {
        String topic = getTopic(event.getClass());
        EventPublisher eventPublisher = PUBLISHER_MAP.get(topic);
        if (eventPublisher != null) {
            eventPublisher.publish(event);
        }
    }

    private static void putIfAbsent(String topic, Class<? extends Event> subscribeType) {
        EventPublisher publisher = PUBLISHER_MAP.get(topic);
        if (publisher == null) {
            DefaultEventPublisher eventPublisher = new DefaultEventPublisher(subscribeType, BUFFER_SIZE);
            PUBLISHER_MAP.put(topic, eventPublisher);
        }
    }

    private static String getTopic(Class<? extends Event> subscribeType) {
        return String.format("event-%s", subscribeType.getCanonicalName());
    }

    /**
     * Shutdown all event publisher in notifier.
     */
    private static void shutdown() {
        for (EventPublisher publisher : PUBLISHER_MAP.values()) {
            try {
                publisher.shutdown();
            } catch (RuntimeException e) {
                Loggers.NOTIFY.error("[Notifier] An error occurred when shutdown", e);
            }
        }
    }

}
