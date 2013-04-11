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

package com.intel.cosbench.driver.util;

import java.util.*;

import org.apache.commons.lang.math.RandomUtils;

/**
 * This class encapsulates logic to pick up operations.
 * 
 * @author ywang19, qzheng7
 * 
 */
public class OperationPicker {

    private int last;
    private Map<Integer, String> ops;

    public OperationPicker() {
        Map<Integer, String> ops = new LinkedHashMap<Integer, String>();
        this.ops = ops;
        this.last = 0;
    }

    public void addOperation(String op, int ratio) {
        if (ratio <= 0)
            return;
        last += ratio;
        ops.put(last, op);
    }

    public String pickOperation(Random random) {
        int r = RandomUtils.nextInt(random, 100) + 1;
        for (Map.Entry<Integer, String> entry : ops.entrySet())
            if (r <= entry.getKey())
                return entry.getValue();
        throw new IllegalStateException();
    }

}
