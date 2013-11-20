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
 * This class is to generate size, which supports "10MB" like size form.
 * 
 * @author ywang19, qzheng7
 * 
 */
class DefaultSizeGenerator implements SizeGenerator {

    private long base;
    private IntGenerator generator;

    public DefaultSizeGenerator() {
        /* empty */
    }

    public long setUnit(String unit) {
        if (StringUtils.endsWith(unit, "GB"))
            return (base = 1000 * 1000 * 1000);
        if (StringUtils.endsWith(unit, "MB"))
            return (base = 1000 * 1000);
        if (StringUtils.endsWith(unit, "KB"))
            return (base = 1000);
        if (StringUtils.endsWith(unit, "B"))
            return (base = 1);
        String msg = "unrecognized size unit: " + unit;
        throw new ConfigException(msg);
    }

    public void setGenerator(IntGenerator generator) {
        this.generator = generator;
    }

    @Override
    public long next(Random random) {
        return generator.next(random) * base;
    }

}
