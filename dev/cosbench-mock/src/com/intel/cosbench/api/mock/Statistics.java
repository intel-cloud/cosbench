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

package com.intel.cosbench.api.mock;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import com.intel.cosbench.log.Logger;

class Statistics {

    private static final Object LOCK = new Object();

    private AtomicInteger global;
    private Map<String, Profile> stats;

    public Statistics() {
        global = new AtomicInteger(0);
        stats = new LinkedHashMap<String, Profile>();
    }

    public void addProfile(String op) {
        stats.put(op, new Profile(op));
    }

    public void addEvent(String op, String key) {
        global.getAndIncrement();
        stats.get(op).addEvent(key);
    }

    public void printStats(Logger logger) {
        synchronized (LOCK) {
            doPrintStats(logger);
        }
    }

    private void doPrintStats(Logger logger) {
        logger.debug("===================================");
        if (global.intValue() > 0)
            for (Profile stat : stats.values())
                stat.printStat(global.doubleValue(), logger);
        logger.debug("===================================");
    }

}
