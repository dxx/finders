package com.dxx.finders.notify;

import org.junit.jupiter.api.Test;

/**
 * Notifier test.
 *
 * @author dxx
 */
public class NotifierTest {

    @Test
    public void testNotify() {
        TestSubscriber subscriber = new TestSubscriber();

        Notifier.registerSubscriber(subscriber);

        Notifier.registerSubscriber(new TestSubscriber2());

        Notifier.publishEvent(new TestEvent("Test Event"));

        Notifier.publishEvent(new TestEvent2("Test Event2"));
    }

}
