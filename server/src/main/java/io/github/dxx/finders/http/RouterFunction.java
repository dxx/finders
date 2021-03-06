package io.github.dxx.finders.http;

import io.github.dxx.finders.cluster.DistributionManager;
import io.github.dxx.finders.cluster.ServerNodeManager;
import io.github.dxx.finders.cluster.ServerStatusManager;
import io.github.dxx.finders.console.ConsoleHandler;
import io.github.dxx.finders.core.ServiceManager;
import io.github.dxx.finders.core.SyncManager;
import io.github.dxx.finders.exception.FindersRuntimeException;
import io.github.dxx.finders.handler.InstanceHandler;
import io.github.dxx.finders.handler.ServerHandler;
import io.github.dxx.finders.handler.ServiceHandler;
import io.github.dxx.finders.http.annotation.RequestMapping;
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

        new ServerStatusManager(serverNodeManager);

        InstanceHandler instanceHandler = new InstanceHandler(serviceManager);
        ServiceHandler serviceHandler = new ServiceHandler(serviceManager, syncManager);
        ServerHandler serverHandler = new ServerHandler();
        this.routeIfNecessary(instanceHandler);
        this.routeIfNecessary(serviceHandler);
        this.routeIfNecessary(serverHandler);

        ConsoleHandler consoleHandler = new ConsoleHandler(serviceManager, serverNodeManager);
        this.routeIfNecessary(consoleHandler);
    }

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
            default:
                throw new FindersRuntimeException("Unsupported request method " + method);
        }
    }
}
