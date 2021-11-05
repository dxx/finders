package com.dxx.finders.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Store service.
 *
 * @author dxx
 */
public class ServiceStore {

    private final Map<String, List<Instance>> storeMap = new ConcurrentHashMap<>(1024);

    public List<Instance> get(String key) {
        return this.storeMap.get(key);
    }

    public void put(String key, List<Instance> instances) {
        this.storeMap.put(key, instances);
    }

    public List<Instance> remove(String key) {
        return this.storeMap.remove(key);
    }

    public List<String> getKeys() {
        return new ArrayList<>(this.storeMap.keySet());
    }

}
