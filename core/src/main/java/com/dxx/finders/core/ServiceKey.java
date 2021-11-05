package com.dxx.finders.core;

import com.dxx.finders.util.StringUtils;

/**
 * Key for service.
 *
 * @author dxx
 */
public class ServiceKey {

    private static final String KEY_PREFIX = "finders.";

    public static final String NAMESPACE_KEY_CONNECTOR = "#";

    public static String build(String namespace, String serviceName) {
        return KEY_PREFIX + namespace + NAMESPACE_KEY_CONNECTOR + serviceName;
    }

    public static String getNamespace(String key) {
        return getNamespaceAndServiceName(key)[0];
    }

    public static String getServiceName(String key) {
        return getNamespaceAndServiceName(key)[1];
    }

    private static String[] getNamespaceAndServiceName(String key) {
        String[] strings = new String[]{"", ""};
        if (StringUtils.isNotEmpty(key)) {
            String[] str = key.split(NAMESPACE_KEY_CONNECTOR);
            strings[0] = str[0].substring(str[0].lastIndexOf(KEY_PREFIX) + 1);
            strings[1] = str[1];
        }
        return strings;
    }
}
