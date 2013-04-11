/** 
 
Copyright 2013 Intel Corporation, All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. 
*/ 

package com.intel.cosbench.config.common;

import org.apache.commons.configuration.Configuration;

import com.intel.cosbench.config.*;

/**
 * One COSBench implementation of Config interface, which encapsulates differences of different configuration format.
 * 
 * @author ywang19, qzheng7
 *
 */
class COSBConfigApator implements Config {

    private Configuration config;

    public COSBConfigApator(Configuration config) {
        this.config = config;
    }

    @Override
    public String get(String key) {
        checkKey(key);
        return config.getString(key);
    }

    @Override
    public String get(String key, String value) {
        return config.getString(key, value);
    }

    @Override
    public int getInt(String key) {
        checkKey(key);
        return config.getInt(key);
    }

    @Override
    public int getInt(String key, int value) {
        return config.getInt(key, value);
    }

    @Override
    public long getLong(String key) {
        checkKey(key);
        return config.getLong(key);
    }

    @Override
    public long getLong(String key, long value) {
        return config.getLong(key, value);
    }

    @Override
    public double getDouble(String key) {
        checkKey(key);
        return config.getDouble(key);
    }

    @Override
    public double getDouble(String key, double value) {
        return config.getDouble(key, value);
    }

    @Override
    public boolean getBoolean(String key) {
        checkKey(key);
        return config.getBoolean(key);
    }

    @Override
    public boolean getBoolean(String key, boolean value) {
        return config.getBoolean(key, value);
    }

    private void checkKey(String key) {
        if (config.containsKey(key))
            return;
        String msg = "no such key defined: " + key;
        throw new ConfigException(msg);
    }

}
