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
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang.StringUtils;

import com.intel.cosbench.config.ConfigException;

/**
 * This class supplies a thread-save int generator that returns ints from lower to upper, and then restarts at lower
 * again.
 * 
 * 
 * @author Niklas Goerke niklas974@github
 * 
 */
class SequentialIntGenerator implements IntGenerator {

    private int lower;
    private int range;
    private AtomicLong cursor;

    public SequentialIntGenerator(int lower, int upper) {
        if (lower <= 0 || upper <= 0 || lower > upper)
            throw new IllegalArgumentException();
        this.lower = lower;
        this.range = upper - lower + 1; // plus one, because we want upper and lower
        this.cursor = new AtomicLong();
        this.cursor.set(-1);
    }

    @Override
    public int next(Random random) {
        return (int) (lower + (cursor.incrementAndGet() % range));
    }

    @Override
    public int next(Random random, int idx, int all) {
        return next(random);

    }

    public static SequentialIntGenerator parse(String pattern) {
        if (!StringUtils.startsWith(pattern, "s(")) {
            return null;
        } else {
            try {
                return tryParse(pattern);
            } catch (NullPointerException e) {
                String msg = "illegal iteration pattern: " + pattern;
                throw new ConfigException(msg);
            }
        }
    }

    private static SequentialIntGenerator tryParse(String pattern) {
        pattern = StringUtils.substringBetween(pattern, "(", ")");
        String[] args = StringUtils.split(pattern, ",");
        int lower = Integer.parseInt(args[0]);
        int upper = Integer.parseInt(args[1]);
        return new SequentialIntGenerator(lower, upper);
    }
}
