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
import com.intel.cosbench.config.ConfigException;
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
        conNmGen = getConNmGen(config, Boolean.FALSE);
        objNmGen = getObjNmGen(config, Boolean.FALSE);
        this.division = Division.getDivision(division);
    }
    
    public void init4Lister(String division, Config config) {
        conNmGen = getConNmGen(config, Boolean.TRUE);
        objNmGen = getObjNmGen(config, Boolean.TRUE);
        this.division = Division.getDivision(division);
	}

    private static NameGenerator getConNmGen(Config config, boolean isLister) {
        String pattern = isLister ? config.get("containers", null)
        		: config.get("containers");
        if (pattern == null)
			return null;
        String prefix = config.get("cprefix", CONTAINER_PREFIX);
        String suffix = config.get("csuffix", CONTAINER_SUFFIX);
        return Generators.getNameGenerator(pattern, prefix, suffix);
    }

    private static NameGenerator getObjNmGen(Config config, boolean isLister) {
        String pattern = isLister ? config.get("objects", null)
        		: config.get("objects");
        if (pattern == null)
			return null;
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
    
    /* a path picker for Lister */
    public String[] pickTargetPath(Random random, int idx, int all) {
		synchronized (this) {
			if (conNmGen == null && objNmGen != null) {
				throw new ConfigException("no such key defined: " + "containers"); 
			} else if (conNmGen == null && objNmGen == null) {
				return new String[] { "", "" };
			} else if (objNmGen == null) {
				return new String[] { conNmGen.next(random, idx, all), "" };
			} else {
				return new String[] { conNmGen.next(random), objNmGen.next(random) };
			}
		}
	}

}
