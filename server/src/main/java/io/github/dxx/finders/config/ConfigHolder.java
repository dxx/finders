package io.github.dxx.finders.config;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The config holder.
 *
 * @author dxx
 */
public class ConfigHolder {

    private static final AtomicBoolean alreadySetConfig = new AtomicBoolean(false);
    private static FindersConfig findersConfig = new FindersConfig();

    public static void setConfig(FindersConfig findersConfig) {
        if (alreadySetConfig.compareAndSet(false, true)) {
            ConfigHolder.findersConfig = findersConfig;
        }
    }

    public static FindersConfig config() {
        return findersConfig;
    }

    private ConfigHolder() {}

}
