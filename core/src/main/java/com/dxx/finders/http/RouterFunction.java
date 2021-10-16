package com.dxx.finders.http;

import com.dxx.finders.constant.Loggers;
import com.dxx.finders.exception.FindersRuntimeException;
import com.dxx.finders.handler.BadRequestHandler;
import com.dxx.finders.handler.HelloHandler;
import com.dxx.finders.handler.ServiceHandler;
import com.dxx.finders.http.annotation.RequestMapping;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.util.Arrays;

/**
 * Builder of the handler functions.
 *
 * @author dxx
 */
public class RouterFunction {

    private final AbstractFilter filterChain;

    private final Router router;

    private RouterFunction(Router router) {
        this.filterChain = FilterBuilder.build();
        this.router = router;
    }

    /**
     * Initialize http routes.
     */
    public static void init(Router router) {
        RouterFunction routerFunction = new RouterFunction(router);

        HelloHandler helloHandler = new HelloHandler();
        routerFunction.routeIfNecessary(helloHandler);

        ServiceHandler serviceHandler = new ServiceHandler();
        routerFunction.routeIfNecessary(serviceHandler);

        router.route().handler(ctx -> new BadRequestHandler().accept(ctx));
    }

    @SuppressWarnings("unchecked")
    private void routeIfNecessary(Object obj) {
        Arrays.stream(obj.getClass().getMethods()).forEach(method -> {
            RequestMapping annotation = method.getAnnotation(RequestMapping.class);
            if (annotation == null) {
                return;
            }
            if (method.getParameterCount() != 1 ||
                    !RoutingContext.class.isAssignableFrom(method.getParameterTypes()[0])) {
                throw new FindersRuntimeException(
                        String.format("The handle method %s#%s must have a parameter and type RoutingContext",
                        obj.getClass().getName(), method.getName()));
            }
            if (HandlerMethodMap.contains(annotation.method().toString(), annotation.path())) {
                throw new FindersRuntimeException(String.format("The handle method already exists for %s %s",
                        annotation.method().toString(), annotation.path()));
            }
            HandlerMethodMap.put(annotation.method().toString(), annotation.path(), method);

            HandlerFunction handlerFunction = (context) -> {
                try {
                    method.invoke(obj, context);
                } catch (Exception e) {
                    if (Loggers.CORE.isErrorEnabled()) {
                        Loggers.CORE.error("ERROR: ", e);
                    }
                    HttpServerResponse response = context.response();
                    response.setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
                    String err = e.getMessage() != null ? e.getMessage() : e.getCause().getMessage();
                    response.end(err);
                }
            };
            this.route(annotation.path(), annotation.method(), handlerFunction);
        });
    }

    private void route(String path, RequestMethod method, HandlerFunction handlerFunction) {
        Handler<RoutingContext> routeHandler = (ctx) ->
                new HandlerAdapter(filterChain, ctx, handlerFunction).apply();
        if (method == RequestMethod.GET) {
            this.router.get(path).handler(routeHandler);
        } else if (method == RequestMethod.POST) {
            this.router.post(path).handler(routeHandler);
        } else if (method == RequestMethod.PUT) {
            this.router.put(path).handler(routeHandler);
        } else if (method == RequestMethod.DELETE) {
            this.router.delete(path).handler(routeHandler);
        }
    }

}
