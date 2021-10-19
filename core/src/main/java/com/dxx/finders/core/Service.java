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

    private String serviceName;

}
