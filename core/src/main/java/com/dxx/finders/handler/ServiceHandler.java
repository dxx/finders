package com.dxx.finders.handler;

import com.dxx.finders.constant.Path;
import com.dxx.finders.constant.Result;
import com.dxx.finders.http.RequestMethod;
import com.dxx.finders.http.annotation.Distribute;
import com.dxx.finders.http.annotation.RequestMapping;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

/**
 * Service handler.
 *
 * @author dxx
 */
public class ServiceHandler {

    @RequestMapping(path = Path.SERVICE_LIST, method = RequestMethod.GET)
    public void list(RoutingContext context) {
        HttpServerResponse response = context.response();
        response.putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON + ";charset=utf-8");
        response.end(Result.SUCCESS);
    }

    @Distribute
    @RequestMapping(path = Path.SERVICE, method = RequestMethod.POST)
    public void register(RoutingContext context) {
        HttpServerResponse response = context.response();
        response.end(Result.SUCCESS);
    }

    @Distribute
    @RequestMapping(path = Path.SERVICE, method = RequestMethod.DELETE)
    public void deregister(RoutingContext context) {
        HttpServerResponse response = context.response();
        response.end(Result.SUCCESS);
    }

}
