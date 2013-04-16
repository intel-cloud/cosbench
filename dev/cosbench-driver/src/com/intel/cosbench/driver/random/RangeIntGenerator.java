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

package com.intel.cosbench.driver.random;

import java.util.Random;

import org.apache.commons.lang.StringUtils;

import com.intel.cosbench.config.ConfigException;

class RangeIntGenerator implements IntGenerator {

    private int lower;
    private int upper;
    
    private int cursor;

    public RangeIntGenerator(int lower, int upper) {
        if (lower <= 0 || upper <= 0 || lower > upper)
            throw new IllegalArgumentException();
        this.lower = lower;
        this.upper = upper;
        
        this.cursor = 0;
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
    	int limit = segment + offset + lower;
    	cursor = cursor <= 0 ? limit - segment : cursor + 1;
    	
    	return cursor < limit ? cursor : -1;
    }
    
    public static RangeIntGenerator parse(String pattern) {
        try {
            return tryParseOld(pattern);
        } catch (Exception e1) {
            if (!StringUtils.startsWith(pattern, "r("))
                return null;
            try {
                return tryParse(pattern);
            } catch (Exception e2) {
            }
        }
        String msg = "illegal iteration pattern: " + pattern;
        throw new ConfigException(msg);
    }

    private static RangeIntGenerator tryParse(String pattern) {
        pattern = StringUtils.substringBetween(pattern, "(", ")");
        String[] args = StringUtils.split(pattern, ",");
        int lower = Integer.parseInt(args[0]);
        int upper = Integer.parseInt(args[1]);
        return new RangeIntGenerator(lower, upper);
    }

    private static RangeIntGenerator tryParseOld(String pattern) {
        String[] args = StringUtils.split(pattern, '-');
        int lower = Integer.parseInt(args[0]);
        int upper = Integer.parseInt(args[1]);
        return new RangeIntGenerator(lower, upper);
    }
}
