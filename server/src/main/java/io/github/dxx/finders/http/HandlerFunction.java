package io.github.dxx.finders.http;

import io.vertx.ext.web.RoutingContext;

import java.util.function.Consumer;

/**
 * Handler function signature.
 *
 * @author dxx
 */
@FunctionalInterface
public interface HandlerFunction extends Consumer<RoutingContext> {}