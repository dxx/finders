package com.dxx.finders.core;

import com.dxx.finders.util.MD5Utils;

import java.util.ArrayList;
import java.util.Collections;
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

    public String getCheckInfo(String key) {
        List<Instance> instances = this.storeMap.get(key);
        if (instances == null) {
            return "";
        }
        Collections.sort(instances);
        StringBuilder stringBuilder = new StringBuilder();
        for (Instance instance : instances) {
            String str = String.format("%s_%s_%s_%s",
                    instance.getCluster(), instance.getIp(), instance.getPort(), instance.getStatus().toString());
            stringBuilder.append(str).append(",");
        }
        return MD5Utils.getMD5String(stringBuilder.toString());
    }

}
