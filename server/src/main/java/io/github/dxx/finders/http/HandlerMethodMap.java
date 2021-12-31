package io.github.dxx.finders.http;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Handler method container.
 *
 * @author dxx
 */
public class HandlerMethodMap {

    public static final Map<String, Method> methodMap = new HashMap<>();

    public static void put(String requestMethod, String path, Method method) {
        methodMap.put(getUrlKey(requestMethod, path), method);
    }

    public static Method get(String requestMethod, String path) {
        return methodMap.get(getUrlKey(requestMethod, path));
    }

    public static boolean contains(String requestMethod, String path) {
        return methodMap.containsKey(getUrlKey(requestMethod, path));
    }

    private static String getUrlKey(String method, String path) {
        return String.format("%s %s", method, path);
    }
}
