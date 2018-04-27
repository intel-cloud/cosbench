package com.joyent.manta.cosbench.config;

import com.intel.cosbench.config.Config;
import com.intel.cosbench.log.LogFactory;
import com.intel.cosbench.log.Logger;
import com.joyent.manta.config.ConfigContext;

/**
 * Cosbench specific implementation of {@link ConfigContext} that allows us
 * to connect Cosbench config seamlessly.
 *
 * @author <a href="https://github.com/dekobon">Elijah Zupancic</a>
 */
public class CosbenchMantaConfigContext implements ConfigContext {
    /**
     * Logger instance.
     */
    private static Logger logger = LogFactory.getSystemLogger();

    /**
     * Embedded Cosbench config reference.
     */
    private final Config config;

    /**
     * Default constructor that wraps a Cosbench config instance.
     * @param config cosbench config instance
     */
    public CosbenchMantaConfigContext(final Config config) {
        this.config = config;
    }

    @Override
    public String getMantaURL() {
        // COSBench throws errors when it doesn't have a config key.
        // This is our hacky workaround.
        try {
            return config.get("url");
        } catch (RuntimeException e) {
            logger.trace("Couldn't get url from COSBench config", e);
            return null;
        }
    }

    @Override
    public String getMantaUser() {
        try {
            return config.get("username");
        } catch (RuntimeException e) {
            logger.trace("Couldn't get username from COSBench config", e);
            return null;
        }
    }

    @Override
    public String getMantaKeyId() {
        try {
            return config.get("fingerprint");
        } catch (RuntimeException e) {
            logger.trace("Couldn't get fingerprint from COSBench config", e);
            return null;
        }
    }

    @Override
    public String getMantaKeyPath() {
        try {
            return config.get("key_path");
        } catch (RuntimeException e) {
            logger.trace("Couldn't get key_path from COSBench config", e);
            return null;
        }
    }

    @Override
    public String getPrivateKeyContent() {
        // We don't support embedded key content with COSBench
        return null;
    }

    @Override
    public String getPassword() {
        try {
            return config.get("password");
        } catch (RuntimeException e) {
            logger.trace("Couldn't get password from COSBench config", e);
            return null;
        }
    }

    @Override
    public Integer getTimeout() {
        return safeGetInteger(
                "timeout", "Couldn't get timeout from COSBench config");
    }

    @Override
    public Integer getRetries() {
        return safeGetInteger(
                "retries", "Couldn't get retries from COSBench config");
    }

    /**
     * Utility method that checks for the presence of Integer values in
     * the COSBench configuration and then returns the value if found.
     * @param key key to check for
     * @param message message to display when value isn't present
     * @return null if not found, otherwise configuration value
     */
    private Integer safeGetInteger(final String key, final String message) {
        try {
            int configValue = config.getInt(key, Integer.MIN_VALUE);

            if (configValue != Integer.MIN_VALUE) {
                return configValue;
            } else {
                return null;
            }

        } catch (RuntimeException e) {
            logger.trace(message, e);
            return null;
        }
    }

    @Override
    public String getMantaHomeDirectory() {
        return ConfigContext.deriveHomeDirectoryFromUser(getMantaUser());
    }
}
