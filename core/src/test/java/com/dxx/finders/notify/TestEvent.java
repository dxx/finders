package com.dxx.finders.notify;

/**
 * @author dxx
 */
public class TestEvent implements Event {

    private String name;

    public TestEvent(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "TestEvent{" +
                "name='" + name + '\'' +
                '}';
    }
}
