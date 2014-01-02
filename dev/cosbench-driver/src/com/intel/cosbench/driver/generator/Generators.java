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

import com.intel.cosbench.config.ConfigException;

public class Generators {

    public static NameGenerator getNameGenerator(String pattern, String prefix,
            String suffix) {
        NumericNameGenerator generator = new NumericNameGenerator();
        generator.setPrefix(prefix);
        generator.setSuffix(suffix);
        generator.setGenerator(getIntGenerator(pattern));
        return generator;
    }

    public static SizeGenerator getSizeGenerator(String pattern) {
        DefaultSizeGenerator generator = new DefaultSizeGenerator();
        generator.setUnit(pattern);
        generator.setGenerator(getIntGenerator(pattern));
        return generator;
    }

    private static IntGenerator getIntGenerator(String pattern) {
        IntGenerator generator = null;
        if ((generator = ConstantIntGenerator.parse(pattern)) != null)
            return generator;
        if ((generator = UniformIntGenerator.parse(pattern)) != null)
            return generator;
        if ((generator = RangeIntGenerator.parse(pattern)) != null)
            return generator;
        if ((generator = SequentialIntGenerator.parse(pattern)) != null)
            return generator;
        if ((generator = HistogramIntGenerator.parse(pattern)) != null)
            return generator;
        String msg = "unrecognized distribution: " + pattern;
        throw new ConfigException(msg);
    }

}
