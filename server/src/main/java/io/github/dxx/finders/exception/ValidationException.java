package io.github.dxx.finders.exception;

/**
 * The parameter validation exception.
 *
 * @author dxx
 */
public class ValidationException extends FindersRuntimeException {

    private final int errorCode;

    private final String errorMsg;

    public ValidationException(int errorCode, String errorMsg) {
        super(errorMsg);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

}
