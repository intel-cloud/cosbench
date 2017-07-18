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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * @author seibed
 *
 */
public class RandomDateGenerator extends MetadataGenerator {

    private final SimpleDateFormat simpleDateFormat;
    private final Random random;
    private final long minimum;
    private final long difference;
    private final Date date = new Date();

    /**
     * @param simpleDateFormat
     * @param random
     * @param valuesArray
     * @param minimum
     * @param maximum
     */
    public RandomDateGenerator(SimpleDateFormat simpleDateFormat, Random random, String[] valuesArray, long minimum,
            long maximum) {
        super(random, valuesArray);
        this.simpleDateFormat = simpleDateFormat;
        this.random = random;
        this.minimum = minimum;
        this.difference = maximum - minimum;
    }

    /* (non-Javadoc)
     * @see com.emc.ecs.cosbench.MetadataGenerator#specificNextString()
     */
    @Override
    protected String specificNextString() {
        date.setTime((long) (minimum + difference * random.nextDouble()));
        return simpleDateFormat.format(date);
    }

}
