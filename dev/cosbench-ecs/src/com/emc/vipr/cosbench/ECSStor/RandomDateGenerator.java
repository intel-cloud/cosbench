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

package com.emc.vipr.cosbench.ECSStor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.PrimitiveIterator.OfLong;

/**
 * @author seibed
 *
 */
public class RandomDateGenerator {

    private final SimpleDateFormat simpleDateFormat;
    private final OfLong ofLong;
    private final Date date = new Date();

    /**
     * @param simpleDateFormat
     * @param ofLong
     */
    public RandomDateGenerator(SimpleDateFormat simpleDateFormat, OfLong ofLong) {
        this.simpleDateFormat = simpleDateFormat;
        this.ofLong = ofLong;
    }

    /**
     * @return
     */
    public synchronized String nextDate() {
        date.setTime(ofLong.nextLong());
        return simpleDateFormat.format(date);
    }

}
