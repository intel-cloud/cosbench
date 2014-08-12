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

package com.intel.cosbench.driver.operator;

import org.apache.commons.lang.StringUtils;

import com.intel.cosbench.config.*;

public class Operators {

	public static Operator getOperator(Operation op, Config config) {
        AbstractOperator operator = createOperator(op.getType());
        operator.init(op.getId(), op.getRatio(), op.getDivision(), config);
        return operator;
    }

    private static AbstractOperator createOperator(String type) {
        if (StringUtils.equals(type, Reader.OP_TYPE))
            return new Reader();
        if (StringUtils.equals(type, Writer.OP_TYPE))
            return new Writer();
        if (StringUtils.equals(type, Lister.OP_TYPE))
            return new Lister();
        if (StringUtils.equals(type, FileWriter.OP_TYPE))
            return new FileWriter();
        if (StringUtils.equals(type, Preparer.OP_TYPE))
            return new Preparer();
        if (StringUtils.equals(type, Cleaner.OP_TYPE))
            return new Cleaner();
        if (StringUtils.equals(type, Initializer.OP_TYPE))
            return new Initializer();
        if (StringUtils.equals(type, Disposer.OP_TYPE))
            return new Disposer();
        if (StringUtils.equals(type, Deleter.OP_TYPE))
            return new Deleter();
        String msg = "unrecognized operation: " + type;
        throw new ConfigException(msg);
    }

}
