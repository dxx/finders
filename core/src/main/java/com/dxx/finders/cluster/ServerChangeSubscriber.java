package com.dxx.finders.cluster;

import com.dxx.finders.notify.Event;
import com.dxx.finders.notify.Subscriber;

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
