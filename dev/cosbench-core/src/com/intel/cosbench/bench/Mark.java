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

package com.intel.cosbench.bench;

import com.intel.cosbench.utils.MapRegistry.Item;


/**
 * The class aggregates performance data in one sample interval.
 * 
 * @author ywang19, qzheng7
 *
 */
public class Mark implements Cloneable, Item {

    private String name; /* mark id */

    private String opType; /* operation type */
    private String sampleType; /* sample type */

    private int opCount; /* number of successful operations */
    private int sampleCount; /* number of successful samples */
    private int totalOpCount; /* number of total operations */
    private int totalSampleCount; /* number of total samples */

    private long rtSum; /* total response time */
    private long byteCount; /* total bytes transferred */

    public Mark() {
        /* empty */
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOpType() {
        return opType;
    }

    public void setOpType(String opType) {
        this.opType = opType;
    }

    public String getSampleType() {
        return sampleType;
    }

    public void setSampleType(String sampleType) {
        this.sampleType = sampleType;
    }

    public int getOpCount() {
        return opCount;
    }

    public void setOpCount(int opCount) {
        this.opCount = opCount;
    }

    public int getSampleCount() {
        return sampleCount;
    }

    public void setSampleCount(int sampleCount) {
        this.sampleCount = sampleCount;
    }

    public int getTotalOpCount() {
        return totalOpCount;
    }

    public void setTotalOpCount(int totalOpCount) {
        this.totalOpCount = totalOpCount;
    }

    public int getTotalSampleCount() {
        return totalSampleCount;
    }

    public void setTotalSampleCount(int totalSampleCount) {
        this.totalSampleCount = totalSampleCount;
    }

    public long getRtSum() {
        return rtSum;
    }

    public void setRtSum(long rtSum) {
        this.rtSum = rtSum;
    }

    public long getByteCount() {
        return byteCount;
    }

    public void setByteCount(long byteCount) {
        this.byteCount = byteCount;
    }

    public void clear() {
        opCount = 0;
        sampleCount = 0;
        totalOpCount = 0;
        totalSampleCount = 0;
        rtSum = 0;
        byteCount = 0;
    }

    public void addSample(Sample sample) {
        if (sample.isSucc())
        {
            sampleCount += 1;
            rtSum += sample.getTime();
            byteCount += sample.getBytes();
        }
        
        totalSampleCount += 1;
    }

    public void addOperation(Result result) {
        if (result.isSucc())
            opCount += 1;
        totalOpCount += 1;
    }

    public static String getMarkType(String opType, String sampleType) {
        return opType + "-" + sampleType;
    }

    public static Mark newMark(String type) {
        String[] types = type.split("-");
        Mark mark = new Mark();
        mark.setName(type);
        mark.setOpType(types[0]);
        mark.setSampleType(types[1]);
        return mark;
    }
}
