package com.dxx.finders.handler;

import com.dxx.finders.constant.Path;
import com.dxx.finders.http.annotation.RequestMapping;
import com.dxx.finders.http.RequestMethod;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

import java.nio.charset.StandardCharsets;

/**
 * Created by dxx on 2021-09-01.
 */
public class HelloHandler {

    @RequestMapping(path = Path.HELLO, method = RequestMethod.GET)
    public void hello(RoutingContext context) {
        HttpServerResponse response = context.response();
        response.putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
        int a = 1 / 0;
    }

    @RequestMapping(path = Path.HELLO1, method = RequestMethod.GET)
    public void hello1(RoutingContext context) {
        context.response().end("Hello1!");
    }

    @RequestMapping(path = Path.DATA, method = RequestMethod.POST)
    public void data(RoutingContext context) {
        System.out.println("哈哈哈哈");
        String body = context.getBodyAsString(StandardCharsets.UTF_8.toString());
        System.out.println(body);
        context.response().end("data!");
    }

}
