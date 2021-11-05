package com.dxx.finders.http;

import com.dxx.finders.cluster.DistributionManager;
import com.dxx.finders.cluster.ServerNodeManager;
import com.dxx.finders.core.ServiceManager;
import com.dxx.finders.core.SyncManager;
import com.dxx.finders.exception.FindersRuntimeException;
import com.dxx.finders.handler.InstanceHandler;
import com.dxx.finders.handler.ServiceHandler;
import com.dxx.finders.http.annotation.RequestMapping;
import io.vertx.core.Handler;
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

        SyncManager syncManager = new SyncManager(serverNodeManager);

        ServiceManager serviceManager = new ServiceManager(syncManager);

        syncManager.init(serviceManager);

        InstanceHandler instanceHandler = new InstanceHandler(serviceManager);
        ServiceHandler serviceHandler = new ServiceHandler(serviceManager, syncManager);
        this.routeIfNecessary(instanceHandler);
        this.routeIfNecessary(serviceHandler);
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
                    throw new FindersRuntimeException("Error occurred while invoke handle", e.getCause());
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
}
