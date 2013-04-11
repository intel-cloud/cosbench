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

import org.apache.commons.lang.StringUtils;

class EmptyIterator implements IntIterator {

    public EmptyIterator() {
        /* empty */
    }

    @Override
    public int next(int cursor) {
        return next(cursor, 1, 1);
    }

    @Override
    public int next(int cursor, int idx, int all) {
        return -1;
    }

    public static EmptyIterator parse(String pattern) {
        if (!StringUtils.equals(pattern, "r(0,0)")
                && !StringUtils.equals(pattern, "0-0"))
            return null;
        return new EmptyIterator();
    }

}
