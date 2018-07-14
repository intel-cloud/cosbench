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
public class RandomDoubleGenerator extends MetadataGenerator {

    private final Random random;
    private final double minimum;
    private final double difference;

    /**
     * @param random
     * @param valuesArray
     * @param minimum
     * @param maximum
     */
    public RandomDoubleGenerator(Random random, String[] valuesArray, double minimum, double maximum) {
        super(random, valuesArray);
        this.random = random;
        this.minimum = minimum;
        this.difference = maximum - minimum;
    }

    /* (non-Javadoc)
     * @see com.emc.ecs.cosbench.MetadataGenerator#specificNextString()
     */
    @Override
    protected String specificNextString() {
        return Double.toString(minimum + difference * random.nextDouble());
    }

}
