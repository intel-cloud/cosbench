/*
 * DummyConfig.java 28.05.2013
 * 
 * Copyright (c) 2013 1&1 Internet AG. All rights reserved.
 * 
 * $Id$
 */
package com.intel.cosbench.api.librados;

import static com.intel.cosbench.client.librados.LibradosConstants.AUTH_PASSWORD_KEY;
import static com.intel.cosbench.client.librados.LibradosConstants.AUTH_USERNAME_KEY;
import static com.intel.cosbench.client.librados.LibradosConstants.ENDPOINT_KEY;

import com.intel.cosbench.config.Config;

public class DummyConfig implements Config {

    private final String endpoint = "192.168.58.121";
    private final String accessKey = "admin";
    private final String secretKey = "AQARiIdRcC5dHRAAjSxlLU8FF6+kDSULyhkMhA==";
    @Override
    public String get(String key) {
        return null;
    }

    @Override
    public String get(String key, String value) {

        if(key == ENDPOINT_KEY) {
            return this.endpoint;
        } else if (key == AUTH_USERNAME_KEY) {
            return this.accessKey;
        } else if (key == AUTH_PASSWORD_KEY) {
            return this.secretKey;
        } else {
            return null;
        }
        
    }

    @Override
    public int getInt(String key) {
        return 0;
    }

    @Override
    public int getInt(String key, int value) {
        return 0;
    }

    @Override
    public long getLong(String key) {
        return 0;
    }

    @Override
    public long getLong(String key, long value) {
        return 0;
    }

    @Override
    public double getDouble(String key) {
        return 0;
    }

    @Override
    public double getDouble(String key, double value) {
        return 0;
    }

    @Override
    public boolean getBoolean(String key) {
        return false;
    }

    @Override
    public boolean getBoolean(String key, boolean value) {
        return false;
    }

}
