package com.dxx.finders.notify;

/**
 * @author dxx
 */
public class TestSubscriber2 implements Subscriber<TestEvent2> {

    @Override
    public void onEvent(TestEvent2 event) {
        System.out.println(event);
    }

    @Override
    public Class<? extends Event> subscribeType() {
        return TestEvent2.class;
    }

}
