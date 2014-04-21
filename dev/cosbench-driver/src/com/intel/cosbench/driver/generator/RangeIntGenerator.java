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

import java.util.LinkedList;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.StringUtils;

import com.intel.cosbench.config.ConfigException;

public class RangeIntGenerator implements IntGenerator {

    private int lower;
    private int upper;

    private AtomicInteger cursors[] = null;

    static class TestThread extends Thread {
        private int all;
        private int idx;
        private IntGenerator generator;
        private Random rnd;

        public TestThread(IntGenerator gen, int idx, int all) {
            this.all = all;
            this.idx = idx;
            this.setName("Thread[" + idx + "]");
            this.generator = gen;
            this.rnd = new Random(123);

        }

        public void run() {
            int i = 0;
            LinkedList<Integer> results = new LinkedList<Integer>();
            try {
                while (i++ < 11) {
                    int answer = generator.next(rnd, idx, all);
                    if (!results.contains(answer)) {
                        System.out.println(this.getName() + ": " + answer);    
                    } else {
                        System.out.println(this.getName() + ": " + answer + " I did get this before!");
                    }
                    results.add(answer);
                    sleep(5);
                }
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        final String pattern = "r(51,1000)";
        final int all = 5;
        int i = 0;
        Vector<TestThread> threads = new Vector<TestThread>();
        RangeIntGenerator gen = RangeIntGenerator.parse(pattern);

        for (i = 0; i < all; i++) {
            TestThread thread = new TestThread(gen, i + 1, all);
            threads.add(thread);
            thread.start();
        }

        try {
            for (i = 0; i < all; i++) {
                threads.elementAt(i).join();
            }
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

    public RangeIntGenerator(int lower, int upper) {
        if (lower <= 0 || upper <= 0 || lower > upper)
            throw new IllegalArgumentException();
        this.lower = lower;
        this.upper = upper;
  
    }

    private synchronized void init(int all) {
    	if(cursors != null) 
    		return;
    	
      	this.cursors = new AtomicInteger[all];

		for (int i = 0; i<all; i++) {
			cursors[i] = new AtomicInteger(0);
		}
    }
    
    @Override
    public int next(Random random) {
        return next(random, 1, 1);
    }

    @Override
    public int next(Random random, int idx, int all) {
    	if(cursors == null)
    		init(all);
    		
        int range = upper - lower + 1;
        int base = range / all;
        int extra = range % all;
        int offset = base * (idx - 1) + (extra >= idx - 1 ? idx - 1 : extra);
        int segment = base + (extra >= idx ? 1 : 0);
        
    	return lower + offset + cursors[idx-1].getAndIncrement() % segment;
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
