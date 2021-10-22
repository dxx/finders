package com.dxx.finders.http;

import com.dxx.finders.cluster.DistributionManager;
import com.dxx.finders.cluster.ServerNodeManager;
import com.dxx.finders.constant.Loggers;
import com.dxx.finders.core.ServiceManager;
import com.dxx.finders.exception.FindersRuntimeException;
import com.dxx.finders.exception.ValidationException;
import com.dxx.finders.handler.HelloHandler;
import com.dxx.finders.handler.InstanceHandler;
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
     * Initialize route handler.
     */
    public static void init(Router router) {
        RouterFunction routerFunction = new RouterFunction(router);
        routerFunction.initRouteHandler();

        router.route().handler(ctx -> new NotFoundHandler().accept(ctx));
    }

    private void initRouteHandler() {
        ServerNodeManager serverNodeManager = ServerNodeManager.init();
        DistributionManager.init(serverNodeManager);

        ServiceManager serviceManager = new ServiceManager();

        HelloHandler helloHandler = new HelloHandler();
        this.routeIfNecessary(helloHandler);

        InstanceHandler instanceHandler = new InstanceHandler(serviceManager);
        this.routeIfNecessary(instanceHandler);
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
                    Loggers.CORE.error("ERROR: ", e);

                    handleMethodInvokeError(context.response(), e);
                }
            };
            this.route(annotation.path(), annotation.method(), handlerFunction);
        });
    }

    private void route(String path, RequestMethod method, HandlerFunction handlerFunction) {
        Handler<RoutingContext> routeHandler = new HandlerDecorator(filterChain, handlerFunction);
        switch (method) {
            case GET:
                this.router.get(path).handler(routeHandler);
                break;
            case POST:
                this.router.post(path).handler(routeHandler);
                break;
            case PUT:
                this.router.put(path).handler(routeHandler);
                break;
            case DELETE:
                this.router.delete(path).handler(routeHandler);
                break;
        }
    }

    private void handleMethodInvokeError(HttpServerResponse response, Exception e) {
        Throwable throwable = e.getCause();
        int statusCode = HttpResponseStatus.INTERNAL_SERVER_ERROR.code();
        String errorMsg = e.getMessage();
        if (throwable != null) {
            errorMsg = throwable.getMessage();
            if (throwable instanceof ValidationException) {
                ValidationException exception = (ValidationException) throwable;
                statusCode = exception.getErrorCode();
                errorMsg = exception.getErrorMsg();
            }
        }
        response.setStatusCode(statusCode);
        response.end(errorMsg);
    }

}
