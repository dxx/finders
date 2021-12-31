package io.github.dxx.finders.handler;

import io.github.dxx.finders.constant.Paths;
import io.github.dxx.finders.constant.Result;
import io.github.dxx.finders.http.RequestMethod;
import io.github.dxx.finders.http.annotation.RequestMapping;
import io.vertx.ext.web.RoutingContext;

/**
 * Server handler.
 *
 * @author dxx
 */
public class ServerHandler {

    /**
     * Get health of server.
     */
    @RequestMapping(path = Paths.SERVER_HEALTH, method = RequestMethod.GET)
    public void health(RoutingContext context) {
        context.end(Result.HEALTH);
    }

}
