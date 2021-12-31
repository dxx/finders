package io.github.dxx.finders.notify;

/**
 * Event publisher.
 *
 * @author dxx
 */
public interface EventPublisher extends Closeable {

    /**
     * Add subscriber.
     *
     * @param subscriber {@link Subscriber}
     */
    void addSubscriber(Subscriber<Event> subscriber);

    /**
     * Remove subscriber.
     *
     * @param subscriber {@link Subscriber}
     */
    void removeSubscriber(Subscriber<Event> subscriber);

    /**
     * Publish an event.
     *
     * @param event {@link Event}
     */
    void publish(Event event);

    /**
     * Notify subscriber.
     *
     * @param subscriber {@link Subscriber}
     * @param event {@link Event}
     */
    void notify(Subscriber<Event> subscriber, Event event);

}
