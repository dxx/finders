package com.dxx.finders.http;

import io.vertx.ext.web.RoutingContext;

/**
 * Filter interface.
 *
 * @author dxx
 */
public interface Filter {

    /**
     * Called before the processor handles it.
     * @param context Request context of server
     * @return Results of the filter execution
     */
    boolean doFilter(RoutingContext context);

    /**
     * Fire the next filter to call doFilter method.
     * @param context Request context of server
     * @return Results of the next filter execution
     */
    boolean fireDoFilter(RoutingContext context);

}
