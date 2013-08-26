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

package com.intel.cosbench.driver.model;

import com.intel.cosbench.bench.*;
import com.intel.cosbench.driver.operator.Operator;
import com.intel.cosbench.log.*;
import com.intel.cosbench.utils.MapRegistry.Item;

/**
 * This class encapsulates operator related information.
 * 
 * @author ywang19, qzheng7
 * 
 */
public class OperatorContext implements Item {

    private static Logger logger = LogFactory.getSystemLogger();

    private Operator operator;
    private Counter counter = Counter.getResCounter();

    public OperatorContext() {
        /* empty */
    }

    @Override
    public String getName() {
        return operator.getName();
    }
    
    public String getId(){
    	return operator.getId();
    }

    public String getOpType() {
        return operator.getOpType();
    }

    public String getSampleType() {
        return operator.getSampleType();
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public void addSample(Sample sample) {
        if (!sample.isSucc())
            return;
        doAddSample(sample);
    }

    private void doAddSample(Sample sample) {
        long time = sample.getTime();
        long start = System.nanoTime();
        counter.doAdd(time); // atomic addition (CPU intensive)
        long end = System.nanoTime();
        double dura = end - start;
        if (dura >= 500000) // if greater than 0.5 milliseconds
            logger.warn("heavy atomic op overhead detected: {}ms",
                    dura / 1000000); // from nanosecond to millisecond
    }

    public Counter getCounter() {
        return counter;
    }

}
