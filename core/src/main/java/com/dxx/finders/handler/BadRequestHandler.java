package com.dxx.finders.handler;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.ext.web.RoutingContext;

import java.util.function.Consumer;

/**
 * Bad request handler.
 *
 * @author dxx
 */
public class BadRequestHandler implements Consumer<RoutingContext> {

    @Override
    public void accept(RoutingContext context) {
        context.response().setStatusCode(HttpResponseStatus.BAD_REQUEST.code());
        context.response().setStatusMessage(HttpResponseStatus.BAD_REQUEST.reasonPhrase());
        context.end();
    }

}
