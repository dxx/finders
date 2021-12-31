package io.github.dxx.finders.notify;

/**
 * @author dxx
 */
public class TestSubscriber implements Subscriber<TestEvent> {

    @Override
    public void onEvent(TestEvent event) {
        System.out.println(event);
    }

    @Override
    public Class<? extends Event> subscribeType() {
        return TestEvent.class;
    }

}
