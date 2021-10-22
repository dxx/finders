package com.dxx.finders.util;

import com.dxx.finders.exception.ValidationException;
import com.fasterxml.jackson.databind.JsonNode;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;

import java.nio.charset.StandardCharsets;

/**
 * Parameter utils.
 *
 * @author dxx
 */
public class ParamUtils {

    /**
     * Get parameter value from HttpServerRequest, if not found will throw {@link ValidationException}.
     *
     * @param request {@link HttpServerRequest}
     * @param name    name
     * @return value
     */
    public static String required(HttpServerRequest request, String name) {
        String value = request.getParam(name);
        if (StringUtils.isEmpty(value)) {
            throw new ValidationException(HttpResponseStatus.BAD_REQUEST.code(), "Param '" + name + "' is required");
        }
        return value;
    }

    /**
     * Get parameter value from HttpServerRequest, if not found will return default value.
     *
     * @param request      {@link HttpServerRequest}
     * @param name         name
     * @param defaultValue default value
     * @return value
     */
    public static String optional(HttpServerRequest request, String name, String defaultValue) {
        String value = request.getParam(name);
        if (StringUtils.isEmpty(value)) {
            return defaultValue;
        }
        return value;
    }

    /**
     * Check a parameter, if null will throw {@link ValidationException}.
     * @param name    parameter name
     * @param value   parameter value
     */
    public static void requiredCheck(String name, String value) {
        if (StringUtils.isEmpty(value)) {
            throw new ValidationException(HttpResponseStatus.BAD_REQUEST.code(), "Param '" + name + "' is required");
        }
    }

    /**
     * Get request body string from RoutingContext.
     * @param context RoutingContext {@link RoutingContext}
     * @return JsonNode object
     */
    public static JsonNode getBodyAsJsonNode(RoutingContext context) {
        String reqBody = context.getBodyAsString(StandardCharsets.UTF_8.toString());
        return JacksonUtils.toJsonNode(reqBody);
    }

}
