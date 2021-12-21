package com.dxx.finders;

import com.dxx.finders.config.ConfigHolder;
import com.dxx.finders.config.ServerConfig;
import com.dxx.finders.env.EnvConst;
import com.dxx.finders.constant.Loggers;
import com.dxx.finders.env.Environment;
import com.dxx.finders.exception.FindersRuntimeException;
import com.dxx.finders.http.RouterFunction;
import com.dxx.finders.util.StringUtils;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FindersServer {

    /**
     * Run the server.
     */
    public static void run() {
        Environment.init();

        ServerConfig serverConfig = ConfigHolder.config().getServerConfig();

        HttpServerOptions serverOptions = new HttpServerOptions();
        serverOptions.setAcceptBacklog(serverConfig.getBacklog());
        serverOptions.setReceiveBufferSize(serverConfig.getRcvBufSize());
        serverOptions.setSendBufferSize(serverConfig.getSndBufSize());

        Vertx vertx = Vertx.vertx();
        HttpServer httpServer = vertx.createHttpServer(serverOptions);
        Router router = Router.router(vertx);

        initUIRoute(router);

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

    private static void initUIRoute(Router router) {
        String home = System.getProperty(EnvConst.HOME);
        if (home.contains(":")) {
            home = home.substring(home.indexOf(":") + 1);
        }
        final String staticRootPath = home + "/ui";
        router.route("/static/*").handler(StaticHandler.create()
                .setAllowRootFileSystemAccess(true)
                .setWebRoot(staticRootPath));

        // Render the index page
        router.route("/finders/*").handler(ctx -> {
            try {
                ctx.response().putHeader("Content-Type", "text/html; charset=UTF-8");
                ctx.end(new String(Files.readAllBytes(Paths.get(staticRootPath + "/index.html"))));
            } catch (IOException e) {
                throw new FindersRuntimeException(e);
            }
        });

        // CORS handler
        router.route().handler(CorsHandler.create("*")
                .allowedMethod(HttpMethod.GET)
                .allowedMethod(HttpMethod.POST)
                .allowedMethod(HttpMethod.PUT)
                .allowedMethod(HttpMethod.DELETE));
    }

    /**
     * Startup entry point during development.
     */
    public static void main(String[] args) {
        String home = System.getProperty(EnvConst.HOME);
        home = StringUtils.defaultIfEmpty(home,
                System.getProperty("user.dir") + File.separator + "server");
        System.setProperty(EnvConst.HOME, home);

        run();
    }
}
