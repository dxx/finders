package com.dxx.finders.http;

import io.vertx.core.Handler;
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
        // Call the filter processing.
        if (!abstractFilter.doFilter(context)) {
            return;
        }
        handlerFunction.accept(context);
    }

}
