package com.dxx.finders.misc;

/**
 * Async http callback.
 *
 * @author dxx
 */
public interface AsyncHttpCallback<T> {

    /**
     * Callback after the response to ok.
     */
    void onSuccess(T t);

    /**
     * An error occurred during the request.
     */
    void onError(Throwable e);

}
