package com.dxx.finders.http;

import io.vertx.ext.web.RoutingContext;

/**
 * Convert handler to netty handler.
 *
 * @author dxx
 */
public class HandlerAdapter {

    private final AbstractFilter abstractFilter;
    private final RoutingContext context;
    private final HandlerFunction handlerFunction;

    public HandlerAdapter(AbstractFilter abstractFilter,
                          RoutingContext context,
                          HandlerFunction handlerFunction) {
        this.abstractFilter = abstractFilter;
        this.context = context;
        this.handlerFunction = handlerFunction;
    }

    public void apply() {
        // Call the filter processing.
        if (!abstractFilter.doFilter(context)) {
            return;
        }
        handlerFunction.accept(context);
    }

}
