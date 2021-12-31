package io.github.dxx.finders.client;

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

    public FindersRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

}
