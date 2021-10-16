package com.dxx.finders.filter;

import com.dxx.finders.exception.FindersRuntimeException;
import com.dxx.finders.http.AbstractFilter;
import com.dxx.finders.http.HandlerMethodMap;
import com.dxx.finders.http.annotation.Distribute;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;

import java.lang.reflect.Method;

/**
 * The filter for distribute requests to other nodes.
 *
 * @author dxx
 */
public class DistributionFilter extends AbstractFilter {

    @Override
    public boolean doFilter(RoutingContext context) {
        HttpServerRequest request = context.request();
        Method method = HandlerMethodMap.get(request.method().name(), request.path());
        if (method == null) {
            throw new FindersRuntimeException(String.format("No handler for %s %s",
                    request.method().name(), request.path()));
        }
        if (method.isAnnotationPresent(Distribute.class)) {
            System.out.println(method);
        }
        return fireDoFilter(context);
    }
}
