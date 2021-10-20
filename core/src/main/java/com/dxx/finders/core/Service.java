package com.dxx.finders.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Represents the service instance information.
 *
 * @author dxx
 */
public class Service {

    /**
     * Map<cluster, Instances>.
     */
    private Map<String, Set<Instance>> instances = new HashMap<>();

    private String namespace;

    private String serviceName;

    public Service(String namespace, String serviceName) {
        this.namespace = namespace;
        this.serviceName = serviceName;
    }

}
