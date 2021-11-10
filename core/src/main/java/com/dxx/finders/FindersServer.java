package com.dxx.finders;

import com.dxx.finders.config.ConfigHolder;
import com.dxx.finders.config.ServerConfig;
import com.dxx.finders.env.EnvConst;
import com.dxx.finders.constant.Loggers;
import com.dxx.finders.env.Environment;
import com.dxx.finders.http.RouterFunction;
import com.dxx.finders.util.StringUtils;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.LoggerFormat;
import io.vertx.ext.web.handler.LoggerHandler;

import java.io.File;

public class FindersServer {

    /**
     * Run the server.
     */
    public static void run() {
        Environment.init();

        ServerConfig serverConfig = ConfigHolder.config().getServerConfig();

        HttpServerOptions serverOptions = new HttpServerOptions();
        serverOptions.setAcceptBacklog(serverConfig.getBacklog());
        serverOptions.setReceiveBufferSize(serverConfig.getRcvBuf());
        serverOptions.setSendBufferSize(serverConfig.getSndBuf());

        Vertx vertx = Vertx.vertx();
        HttpServer httpServer = vertx.createHttpServer(serverOptions);
        Router router = Router.router(vertx);

        // Logger handler
        router.route().handler(LoggerHandler.create(LoggerFormat.DEFAULT));
        // Body handler
        router.route().handler(BodyHandler.create());

        RouterFunction.init(router);

        httpServer.requestHandler(router);

        httpServer.listen(serverConfig.getPort(), http -> {
            if (http.succeeded()) {
                Loggers.CORE.info("Finders server started on port: {}", http.result().actualPort());
                return;
            }
            Loggers.CORE.error("Finders server startup error", http.cause());
        });
    }

    /**
     * Startup entry point during development.
     */
    public static void main(String[] args) {
        String home = System.getProperty(EnvConst.HOME);
        home = StringUtils.defaultIfEmpty(home,
                System.getProperty("user.dir") + File.separator + "core");
        System.setProperty(EnvConst.HOME, home);

        run();
    }
}
