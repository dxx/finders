package com.dxx.finders.core;

import java.util.Objects;

/**
 * @author dxx
 */
public class Pair<A, B> {

    private final A value0;

    private final B value1;

    public Pair(A value0, B value1) {
        this.value0 = value0;
        this.value1 = value1;
    }

    public static <A, B> Pair<A, B> with(A value0, B value1) {
        return new Pair<>(value0, value1);
    }

    public A getValue0() {
        return value0;
    }

    public B getValue1() {
        return value1;
    }

    @Override
    public int hashCode() {
        return value0.hashCode() * 13 + (value1 == null ? 0 : value1.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof Pair) {
            Pair pair = (Pair) o;
            if (!Objects.equals(value0, pair.value0)) return false;
            if (!Objects.equals(value1, pair.value1)) return false;
            return true;
        }
        return false;
    }

}
