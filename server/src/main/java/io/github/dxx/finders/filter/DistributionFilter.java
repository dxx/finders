package io.github.dxx.finders.filter;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.dxx.finders.cluster.DistributionManager;
import io.github.dxx.finders.constant.Services;
import io.github.dxx.finders.exception.FindersRuntimeException;
import io.github.dxx.finders.http.AbstractFilter;
import io.github.dxx.finders.http.HandlerMethodMap;
import io.github.dxx.finders.http.annotation.Distribute;
import io.github.dxx.finders.misc.FindersHttpClient;
import io.github.dxx.finders.util.ParamUtils;
import io.github.dxx.finders.util.StringUtils;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

import java.lang.reflect.Method;

/**
 * The filter for distribute requests to other nodes.
 *
 * @author dxx
 */
public class DistributionFilter extends AbstractFilter {

    @Override
    public boolean doFilter(RoutingContext context) {
        HttpServerRequest request = context.request();
        HttpServerResponse response = context.response();
        Method method = HandlerMethodMap.get(request.method().name(), request.path());
        if (method == null) {
            throw new FindersRuntimeException(String.format("No handler for %s %s",
                    request.method().name(), request.path()));
        }
        if (DistributionManager.isCluster() && method.isAnnotationPresent(Distribute.class)) {
            String serviceName = request.getParam(Services.PARAM_SERVICE_NAME);
            JsonNode jsonNode = ParamUtils.getBodyAsJsonNode(context);
            if (StringUtils.isEmpty(serviceName)) {
                serviceName = jsonNode.get(Services.PARAM_SERVICE_NAME) != null ?
                        jsonNode.get(Services.PARAM_SERVICE_NAME).asText() : "";
            }
            ParamUtils.requiredCheck(Services.PARAM_SERVICE_NAME, serviceName);

            if (!DistributionManager.isResponsible(serviceName)) {
                String address = DistributionManager.getDistributedAddress(serviceName);
                String result = FindersHttpClient.request(String.format("http://%s%s", address, request.uri()),
                        HttpMethod.valueOf(request.method().name()), jsonNode.toString());
                response.putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON + ";charset=UTF-8");
                response.end(result);
                return false;
            }
        }
        return fireDoFilter(context);
    }
}
