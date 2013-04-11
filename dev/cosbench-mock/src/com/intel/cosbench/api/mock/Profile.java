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

import java.text.DecimalFormat;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import com.intel.cosbench.log.Logger;

class Profile {

    private static final DecimalFormat FT = new DecimalFormat("00.00%");

    private String name;
    private AtomicInteger total;
    private ConcurrentMap<String, AtomicInteger> counters;

    public Profile(String name) {
        this.name = name;
        total = new AtomicInteger(0);
        counters = new ConcurrentSkipListMap<String, AtomicInteger>();
    }

    public void addEvent(String key) {
        total.getAndIncrement();
        if (!counters.containsKey(key))
            counters.putIfAbsent(key, new AtomicInteger(0));
        counters.get(key).getAndIncrement();
    }

    public void printStat(double all, Logger logger) {
        double sum = total.doubleValue();
        logger.debug("----------- " + name + " Statistic ----------");
        for (Map.Entry<String, AtomicInteger> entry : counters.entrySet()) {
            int value = entry.getValue().intValue();
            logger.debug("[" + entry.getKey() + "] [" + value + "] ["
                    + FT.format(value / sum) + "]");
        }
        logger.debug("-----------------------------------");
        logger.debug("Total Ops: " + total.intValue() + " ("
                + FT.format(total.intValue() / all) + ")");
        logger.debug("-----------------------------------");
    }

}