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

class NumericNameGenerator implements NameGenerator {

    private String prefix;
    private String suffix;
    private IntGenerator generator;

    public NumericNameGenerator() {
        /* empty */
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public void setGenerator(IntGenerator generator) {
        this.generator = generator;
    }

    @Override
    public String next(Random random) {
        int value = generator.next(random);
        return StringUtils.join(new Object[] { prefix, value, suffix });
    }
    
    @Override
    public int nextKey(Random random) {
        return generator.next(random);
    }    

    @Override
    public String next(Random random, int idx, int all) {
        int value = generator.next(random, idx, all);
        return StringUtils.join(new Object[] { prefix, value, suffix });
    }

}
