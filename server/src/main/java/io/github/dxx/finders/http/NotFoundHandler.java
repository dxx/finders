package io.github.dxx.finders.http;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.ext.web.RoutingContext;

/**
 * Bad request handler.
 *
 * @author dxx
 */
public class NotFoundHandler implements HandlerFunction {

    @Override
    public void accept(RoutingContext context) {
        context.response().setStatusCode(HttpResponseStatus.NOT_FOUND.code());
        context.response().setStatusMessage(HttpResponseStatus.NOT_FOUND.reasonPhrase());
        context.end(HttpResponseStatus.NOT_FOUND.reasonPhrase());
    }

}
