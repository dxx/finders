package io.github.dxx.finders.notify;

import org.junit.jupiter.api.Test;

/**
 * Publisher test.
 *
 * @author dxx
 */
public class PublisherTest {

    @Test
    public void testPublish() {
        TestSubscriber subscriber = new TestSubscriber();

        DefaultEventPublisher defaultEventPublisher = new DefaultEventPublisher(TestEvent.class, 1024);

        defaultEventPublisher.publish(new TestEvent("Test Event"));

        defaultEventPublisher.addSubscriber(subscriber);
        defaultEventPublisher.addSubscriber(new Subscriber<TestEvent>() {
            @Override
            public void onEvent(TestEvent event) {
                System.out.println("===" + event);
            }

            @Override
            public Class<? extends Event> subscribeType() {
                return TestEvent.class;
            }
        });

        defaultEventPublisher.removeSubscriber(subscriber);

        defaultEventPublisher.publish(new TestEvent("Test Event1"));

        defaultEventPublisher.publish(new TestEvent2("Test Event 222"));

        Runtime.getRuntime().addShutdownHook(new Thread(defaultEventPublisher::shutdown));
    }

}
