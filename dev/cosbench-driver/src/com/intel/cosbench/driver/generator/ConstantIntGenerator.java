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

package com.intel.cosbench.driver.generator;

import java.util.Random;

import org.apache.commons.lang.StringUtils;

import com.intel.cosbench.config.ConfigException;

/**
 * This class is to generate integers, it's for constant distribution.
 * 
 * @author ywang19, qzheng7
 * 
 */
class ConstantIntGenerator implements IntGenerator {

    private int value;

    private ConstantIntGenerator(int value) {
        this.value = value;
    }

    @Override
    public int next(Random random) {
        return value;
    }

    @Override
    public int next(Random random, int idx, int all) {
        return next(random); // division not supported
    }

    public static ConstantIntGenerator parse(String pattern) {
        if (!StringUtils.startsWith(pattern, "c("))
            return null;
        try {
            return tryParse(pattern);
        } catch (Exception e) {
        }
        String msg = "illegal constant distribution pattern: " + pattern;
        throw new ConfigException(msg);
    }

    private static ConstantIntGenerator tryParse(String pattern) {
        pattern = StringUtils.substringBetween(pattern, "(", ")");
        String[] args = StringUtils.split(pattern, ',');
        int value = Integer.parseInt(args[0]);
        return new ConstantIntGenerator(value);
    }

}
