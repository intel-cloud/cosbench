/**
 * Copyright 2017 EMC Corporation. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.emc.ecs.cosbench;

import java.util.Random;

/**
 * @author seibed
 *
 */
public abstract class MetadataGenerator implements IStringGenerator {

    final private String[] values;
    final private Random random;

    /**
     * @param random
     * @param valuesArray
     */
    public MetadataGenerator(Random random, String[] valuesArray) {
        values = valuesArray;
        this.random = random;
    }

    /* (non-Javadoc)
     * @see com.emc.ecs.cosbench.IStringGenerator#nextString()
     */
    @Override
    public final String nextString() {
        if (values != null) {
            return values[random.nextInt(values.length)];
        }
        return specificNextString();
    }

    protected abstract String specificNextString();

}
