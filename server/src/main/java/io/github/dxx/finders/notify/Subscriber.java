package io.github.dxx.finders.notify;

/**
 * Subscriber interface.
 *
 * @author dxx
 */
public interface Subscriber<T extends Event> {

    /**
     * A callback when an event is received.
     *
     * @param event {@link Event}
     */
    void onEvent(T event);

    /**
     * Type of the subscriber's subscription.
     *
     * @return Class which extends {@link Event}
     */
    Class<? extends Event> subscribeType();

}
