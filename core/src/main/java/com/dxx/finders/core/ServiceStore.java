package com.dxx.finders.core;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Store service.
 *
 * @author dxx
 */
public class ServiceStore {

    private static final String KEY_PREFIX = "finders.";

    public static final String NAMESPACE_KEY_CONNECTOR = "#";

    private final Map<String, List<Instance>> storeMap = new ConcurrentHashMap<>(1024);

    public List<Instance> get(String namespace, String serviceName) {
        return this.storeMap.get(buildKey(namespace, serviceName));
    }

    public void put(String namespace, String serviceName, List<Instance> instances) {
        this.storeMap.put(buildKey(namespace, serviceName), instances);
    }

    public List<Instance> remove(String namespace, String serviceName) {
        return this.storeMap.remove(buildKey(namespace, serviceName));
    }

    public String buildKey(String namespace, String serviceName) {
        return KEY_PREFIX + namespace + NAMESPACE_KEY_CONNECTOR + serviceName;
    }

}
