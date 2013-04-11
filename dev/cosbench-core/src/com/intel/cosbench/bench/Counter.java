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

package com.intel.cosbench.bench;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * The class encapsulates the sample counting of response time histogram, the buckets are selected by using 
 * 10 milliseconds as interval and 500 seconds as upper limit.
 * 
 * @author ywang19, qzheng7
 *
 */
public class Counter {

    /* interval for the response time histogram */
    public final static long RES_INT = 10; // 10 milliseconds

    /* limit for the response time histogram */
    public final static long RES_MAX = 500 * 1000; // 500 seconds
    
    /* the index of upper limit */
    public final static int UL = (int) (RES_MAX/RES_INT);

    private AtomicInteger[] counts;

    public Counter(int size) {
        AtomicInteger[] counts = new AtomicInteger[size];
        for (int i = 0; i < size; i++)
            counts[i] = new AtomicInteger(0);
        this.counts = counts;
    }

    public int size() {
        return counts.length;
    }

    public int get(int index) {
        return counts[index].get();
    }

    /**
     * The method counts the time to corresponding bucket.
     * 
     * @param time	the response time to be counted
     * @return
     */
    public void doAdd(long time) {
        int index = (time >= RES_MAX) ? UL : (int) (time / RES_INT);
        counts[index].incrementAndGet();
    }

    public static Counter getResCounter() {
        int size = 1 + UL;
        return new Counter(size);
    }

    public static long[] getResTime(int index) {
        if (index >= UL)
            return new long[] { RES_MAX, Long.MAX_VALUE };
        return new long[] { index * RES_INT, (index + 1) * RES_INT };
    }

}
