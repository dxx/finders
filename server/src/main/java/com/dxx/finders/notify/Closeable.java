package com.dxx.finders.notify;

/**
 * An interface used to define the resource shutdown.
 *
 * @author dxx
 */
public interface Closeable {

    void shutdown() throws RuntimeException;

}
