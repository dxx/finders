package com.dxx.finders.handler;

import com.dxx.finders.constant.Paths;
import com.dxx.finders.constant.Result;
import com.dxx.finders.http.RequestMethod;
import com.dxx.finders.http.annotation.RequestMapping;
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
