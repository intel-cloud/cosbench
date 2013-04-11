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

import static com.intel.cosbench.driver.util.Defaults.*;
import static com.intel.cosbench.driver.util.Division.*;

import com.intel.cosbench.config.Config;
import com.intel.cosbench.driver.iterator.*;

/**
 * This class encapsulates logic to scan object ranges.
 * 
 * @author ywang19, qzheng7
 * 
 */
public class ObjectScanner {

    private Division division;

    private NameIterator conNmIter;
    private NameIterator objNmIter;

    public ObjectScanner() {
        /* empty */
    }

    public void init(String division, Config config) {
        conNmIter = getConNmIter(config);
        objNmIter = getObjNmIter(config);
        this.division = Division.getDivision(division);
    }

    private static NameIterator getConNmIter(Config config) {
        String pattern = config.get("containers");
        String prefix = config.get("cprefix", CONTAINER_PREFIX);
        String suffix = config.get("csuffix", CONTAINER_SUFFIX);
        return Iterators.getNameIterator(pattern, prefix, suffix);
    }

    private static NameIterator getObjNmIter(Config config) {
        String pattern = config.get("objects");
        String prefix = config.get("oprefix", OBJECT_PREFIX);
        String suffix = config.get("osuffix", OBJECT_SUFFIX);
        return Iterators.getNameIterator(pattern, prefix, suffix);
    }

    public String[] nextObjPath(String[] curr, int idx, int all) {
        if (curr == null)
            return initObjPath(idx, all);
        String objName = nextObj(curr[1], idx, all);
        if (objName != null)
            return new String[] { curr[0], objName };
        String conName = nextCon(curr[0], idx, all);
        if (conName != null)
            return new String[] { conName, nextObj(null, idx, all) };
        return null;
    }

    private String nextCon(String curr, int idx, int all) {
        if (!division.equals(CONTAINER))
            return conNmIter.next(curr);
        return conNmIter.next(curr, idx, all);
    }

    private String nextObj(String curr, int idx, int all) {
        if (!division.equals(OBJECT))
            return objNmIter.next(curr);
        return objNmIter.next(curr, idx, all);
    }

    private String[] initObjPath(int idx, int all) {
        return new String[] { nextCon(null, idx, all), nextObj(null, idx, all) };
    }

}
