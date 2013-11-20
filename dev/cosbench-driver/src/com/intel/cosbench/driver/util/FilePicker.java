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

import java.util.Random;

import com.intel.cosbench.config.Config;
import com.intel.cosbench.driver.generator.Generators;
import com.intel.cosbench.driver.generator.NameGenerator;

/**
 * This class encapsulates logic to pick integers for identififaction of files / objects
 * 
 * @author Niklas Goerke - niklas974@github
 * 
 */
public class FilePicker {

    private NameGenerator objNmGen;

    public FilePicker() {
        /* empty */
    }

    public void init(String range, Config config) {
        String selector = config.get("fileselection").substring(0, 1);
        this.objNmGen = Generators.getNameGenerator(selector + range, "", "");
    }

    public int pickObjKey(Random random) {
        return objNmGen.nextKey(random);
    }
}
