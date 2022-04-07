package io.github.dxx.finders.config;

/**
 * The config holder.
 *
 * @author dxx
 */
public class ConfigHolder {

    private static boolean alreadySetConfig = false;

    private static FindersConfig findersConfig;

    public static void setConfig(FindersConfig findersConfig) {
        if (!alreadySetConfig) {
            ConfigHolder.findersConfig = findersConfig;
            alreadySetConfig = true;
        }
    }

    public static FindersConfig config() {
        if (findersConfig == null) {
            throw new IllegalStateException("FindersConfig can't be null");
        }
        return findersConfig;
    }

    private ConfigHolder() {}

}
