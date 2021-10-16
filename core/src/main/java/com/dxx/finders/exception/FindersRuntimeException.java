package com.dxx.finders.exception;

/**
 * The finders runtime exception.
 *
 * @author dxx
 */
public class FindersRuntimeException extends RuntimeException {

    public FindersRuntimeException(String message) {
        super(message);
    }

    public FindersRuntimeException(Throwable cause) {
        super(cause);
    }

}
