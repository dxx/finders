package io.github.dxx.finders.cluster;

import io.github.dxx.finders.notify.Event;
import io.github.dxx.finders.notify.Subscriber;

/**
 * Server change subscriber.
 *
 * @author dxx
 */
public abstract class ServerChangeSubscriber implements Subscriber<ServerChangeEvent> {

    @Override
    public Class<? extends Event> subscribeType() {
        return ServerChangeEvent.class;
    }

}
