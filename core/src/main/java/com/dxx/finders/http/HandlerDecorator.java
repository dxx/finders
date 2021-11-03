package com.dxx.finders.http;

import com.dxx.finders.constant.Loggers;
import com.dxx.finders.exception.FindersRuntimeException;
import com.dxx.finders.exception.ValidationException;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

/**
 * Decorating the handler.
 *
 * @author dxx
 */
public class HandlerDecorator implements Handler<RoutingContext> {

    private final AbstractFilter abstractFilter;
    private final HandlerFunction handlerFunction;

    public HandlerDecorator(AbstractFilter abstractFilter,
                            HandlerFunction handlerFunction) {
        this.abstractFilter = abstractFilter;
        this.handlerFunction = handlerFunction;
    }

    @Override
    public void handle(RoutingContext context) {
        try {
            // Call the filter chain.
            if (!abstractFilter.doFilter(context)) {
                return;
            }
            handlerFunction.accept(context);
        } catch (Exception e) {
            Loggers.CORE.error("ERROR: ", e);

            handleMethodInvokeError(context.response(), e);
        }
    }

    private void handleMethodInvokeError(HttpServerResponse response, Exception e) {
        int statusCode = HttpResponseStatus.INTERNAL_SERVER_ERROR.code();
        String errorMsg = e.getMessage();
        Throwable throwable = e.getCause();
        if (e instanceof FindersRuntimeException && throwable != null) {
            errorMsg = throwable.getMessage();
            if (throwable instanceof ValidationException) {
                ValidationException exception = (ValidationException) throwable;
                statusCode = exception.getErrorCode();
                errorMsg = exception.getErrorMsg();
            }
        }
        response.setStatusCode(statusCode);
        response.end(errorMsg);
    }

}
