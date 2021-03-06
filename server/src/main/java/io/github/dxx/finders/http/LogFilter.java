package io.github.dxx.finders.http;

import io.github.dxx.finders.constant.Loggers;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;

/**
 * The log filter.
 *
 * @author dxx
 */
public class LogFilter extends AbstractFilter {

    public boolean doFilter(RoutingContext context) {
        if (Loggers.CORE.isDebugEnabled()) {
            HttpServerRequest request = context.request();
            Loggers.CORE.debug("{} {} {}", request.method(), request.uri(), request.version());
            MultiMap headers = request.headers();
            for (String name : headers.names()) {
                Loggers.CORE.debug("{}: {}", name, headers.get(name));
            }
        }
        return fireDoFilter(context);
    }

}
