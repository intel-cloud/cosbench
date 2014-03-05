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
import org.apache.commons.lang.math.RandomUtils;

import com.intel.cosbench.config.ConfigException;

/**
 * This class is to generate integers, it's for uniform distribution.
 * 
 * @author ywang19, qzheng7
 * 
 */
class UniformIntGenerator implements IntGenerator {

    private int lower;
    private int upper;
    private static  int MAXupper = Integer.MAX_VALUE;

    public UniformIntGenerator(int lower, int upper) {
        if (lower <= 0 || upper <= 0 || lower > upper)
            throw new IllegalArgumentException();
        this.lower = lower;
        this.upper = upper;
    }

    @Override
    public int next(Random random) {
        return next(random, 1, 1);
    }

    @Override
    public int next(Random random, int idx, int all) {
        int range = upper - lower + 1;
        int base = range / all;
        int extra = range % all;
        int offset = base * (idx - 1) + (extra >= idx - 1 ? idx - 1 : extra);
        int segment = base + (extra >= idx ? 1 : 0);
        int value = RandomUtils.nextInt(random, segment);
        value += offset + lower;
        return value;
    }

    public static UniformIntGenerator parse(String pattern) {
        if (!StringUtils.startsWith(pattern, "u("))
            return null;
        try {
            return tryParse(pattern);
        } catch (Exception e) {
        }
        String msg = "illegal uniform distribution pattern: " + pattern;
        throw new ConfigException(msg);
    }

    private static UniformIntGenerator tryParse(String pattern) {
        pattern = StringUtils.substringBetween(pattern, "(", ")");
        String[] args = StringUtils.split(pattern, ',');
        int lower = Integer.parseInt(args[0]);
        int upper = (args.length == 2) ? Integer.parseInt(args[1]) : MAXupper;
        return new UniformIntGenerator(lower, upper);
    }

    public static int getMAXupper () {
		return MAXupper;
	}

}
