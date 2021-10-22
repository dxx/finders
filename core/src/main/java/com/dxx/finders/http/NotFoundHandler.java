package com.dxx.finders.http;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.ext.web.RoutingContext;

import java.util.function.Consumer;

/**
 * Bad request handler.
 *
 * @author dxx
 */
public class NotFoundHandler implements Consumer<RoutingContext> {

    @Override
    public void accept(RoutingContext context) {
        context.response().setStatusCode(HttpResponseStatus.NOT_FOUND.code());
        context.response().setStatusMessage(HttpResponseStatus.NOT_FOUND.reasonPhrase());
        context.end(HttpResponseStatus.NOT_FOUND.reasonPhrase());
    }

}
