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

import java.util.Random;

import com.intel.cosbench.config.Config;
import com.intel.cosbench.driver.generator.*;

/**
 * This class encapsulates logic to pick up objects.
 * 
 * @author ywang19, qzheng7
 * 
 */
public class ObjectPicker {

    private Division division;

    private NameGenerator conNmGen;
    private NameGenerator objNmGen;

    public ObjectPicker() {
        /* empty */
    }

    public void init(String division, Config config) {
        conNmGen = getConNmGen(config);
        objNmGen = getObjNmGen(config);
        this.division = Division.getDivision(division);
    }

    private static NameGenerator getConNmGen(Config config) {
        String pattern = config.get("containers");
        String prefix = config.get("cprefix", CONTAINER_PREFIX);
        String suffix = config.get("csuffix", CONTAINER_SUFFIX);
        return Generators.getNameGenerator(pattern, prefix, suffix);
    }

    private static NameGenerator getObjNmGen(Config config) {
        String pattern = config.get("objects");
        String prefix = config.get("oprefix", OBJECT_PREFIX);
        String suffix = config.get("osuffix", OBJECT_SUFFIX);
        return Generators.getNameGenerator(pattern, prefix, suffix);
    }

    public String[] pickObjPath(Random random, int idx, int all) {
    	synchronized(this) {
        if (division.equals(OBJECT))
            return new String[] { conNmGen.next(random),
                    objNmGen.next(random, idx, all) };
        if (division.equals(CONTAINER))
            return new String[] { conNmGen.next(random, idx, all),
                    objNmGen.next(random) };
        return new String[] { conNmGen.next(random), objNmGen.next(random) };
    	}
    }

}
