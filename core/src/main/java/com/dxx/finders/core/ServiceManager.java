package com.dxx.finders.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service management.
 *
 * @author dxx
 */
public class ServiceManager {

    /**
     * Map<namespace, Map<serviceName, Service>>.
     */
    private final Map<String, Map<String, Service>> serviceMap = new ConcurrentHashMap<>();

}
