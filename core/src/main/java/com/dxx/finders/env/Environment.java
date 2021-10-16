package com.dxx.finders.env;

import ch.qos.logback.classic.util.ContextInitializer;
import com.dxx.finders.config.ClusterConfig;
import com.dxx.finders.config.ConfigHolder;
import com.dxx.finders.config.FindersConfig;
import com.dxx.finders.config.ServerConfig;
import com.dxx.finders.constant.Loggers;
import com.dxx.finders.exception.FindersRuntimeException;
import com.dxx.finders.util.JacksonUtils;
import com.dxx.finders.util.StringUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.toml.TomlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Environment initialization.
 *
 * @author dxx
 */
public class Environment {

    private static Logger logger;

    public static String HOME_DIR = "";

    public static String CONF_DIR = "";

    /**
     * Initialize the environment and the configuration.
     */
    public static void init() {
        String homeDir = System.getProperty(EnvConst.HOME);
        if (StringUtils.isEmpty(homeDir)) {
            throw new FindersRuntimeException(String.format("The %s is not specified", EnvConst.HOME));
        }

        HOME_DIR = homeDir;
        CONF_DIR = HOME_DIR + File.separator + "conf";

        initLog();

        // Using logger after setting logback config file.
        logger = LoggerFactory.getLogger(Environment.class);
        if (logger.isDebugEnabled()) {
            logger.debug("{}: {}", EnvConst.HOME, HOME_DIR);
        }

        intiConfig();
    }

    public static void initLog() {
        // Setting logback config file. Must setting before use logger.
        System.setProperty(ContextInitializer.CONFIG_FILE_PROPERTY,
                String.format("%s%s%s", CONF_DIR, File.separator, EnvConst.LOGBACK_FILE_NAME));
    }

    private static void intiConfig() {
        String config = String.format("%s%s%s", CONF_DIR, File.separator, EnvConst.CONFIG_FILE_NAME);
        if (logger.isDebugEnabled()) {
            logger.debug("Initialize configuration using the file {}", config);
        }
        try {
            JsonNode jsonNode = TomlMapper.builder().build().readTree(new File(config));
            parseConfig(jsonNode);
        } catch (Exception e) {
            if (Loggers.CORE.isErrorEnabled()) {
                Loggers.CORE.error("Init config error: {}", e.getMessage());
            }
            throw new FindersRuntimeException(e);
        }
    }

    private static void parseConfig(JsonNode jsonNode) {
        FindersConfig findersConfig = new FindersConfig();
        JsonNode serverJsonNode = jsonNode.get("server");
        if (serverJsonNode != null) {
            ServerConfig serverConfig = JacksonUtils.toObject(serverJsonNode.toString(), ServerConfig.class);
            findersConfig.setServerConfig(serverConfig);
        }
        JsonNode clusterJsonNode = jsonNode.get("cluster");
        if (clusterJsonNode != null) {
            ClusterConfig clusterConfig = JacksonUtils.toObject(clusterJsonNode.toString(), ClusterConfig.class);
            findersConfig.setClusterConfig(clusterConfig);
        }
        ConfigHolder.setConfig(findersConfig);
    }

}
