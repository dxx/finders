package com.dxx.finders.constant;

import java.util.concurrent.TimeUnit;

/**
 * Service constants.
 *
 * @author dxx
 */
public class Services {

    public static final String PARAM_NAMESPACE = "namespace";

    public static final String PARAM_CLUSTER = "cluster";

    public static final String PARAM_SERVICE_NAME = "serviceName";

    public static final String DEFAULT_NAMESPACE = "default";

    public static final String DEFAULT_CLUSTER = "DEFAULT_CLUSTER";

    public static final String CLUSTER_SYNTAX = "[0-9a-zA-Z-_]+";

    public static final String SERVICE_NAME_SYNTAX = "[0-9a-zA-Z@\\.:_-]+";

    public static final String ACTION_ADD = "add";

    public static final String ACTION_REMOVE = "remove";

    public static final String ACTION_SYNC = "sync";

    public static final long INSTANCE_HEARTBEAT_TIMEOUT = TimeUnit.SECONDS.toMillis(10);

    public static final long INSTANCE_DELETE_TIMEOUT = TimeUnit.SECONDS.toMillis(20);

}
