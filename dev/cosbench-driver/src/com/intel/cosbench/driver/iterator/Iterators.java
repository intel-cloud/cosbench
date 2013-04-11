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

package com.intel.cosbench.driver.iterator;

import com.intel.cosbench.config.ConfigException;

public class Iterators {

    public static NameIterator getNameIterator(String pattern, String prefix,
            String suffix) {
        NumericNameIterator iterator = new NumericNameIterator();
        iterator.setPrefix(prefix);
        iterator.setSuffix(suffix);
        iterator.setIterator(getIntIterator(pattern));
        return iterator;
    }

    private static IntIterator getIntIterator(String pattern) {
        IntIterator iterator = null;
        if ((iterator = EmptyIterator.parse(pattern)) != null)
            return iterator;
        if ((iterator = RangeIterator.parse(pattern)) != null)
            return iterator;
        String msg = "unrecognized range expression: " + pattern;
        throw new ConfigException(msg);
    }

}
