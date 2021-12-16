package com.dxx.finders.notify;

/**
 * @author dxx
 */
public class TestEvent2 implements Event {

    private String name;

    public TestEvent2(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "TestEvent2{" +
                "name='" + name + '\'' +
                '}';
    }
}
