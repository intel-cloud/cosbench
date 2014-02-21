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

import java.util.Date;

/**
 * This class encapsulates the performance sample of one operation.
 * 
 * @author ywang19, qzheng7
 *
 */
public class Sample {

    private Date timestamp;

    private String opName;
    private boolean succ;
    private String opType;
    private String opId;
    private String sampleType;

    private long time; /* response time */
    private long xferTime; /* transfer time */
    private long bytes; /* bytes transferred */

	public Sample(Date timestamp, String opId, String opType,
			String sampleType, String opName, boolean succ) {
		this(timestamp, opId, opType, sampleType, opName, succ, 0L, 0L, 0L);
    }

	public Sample(Date timestamp, String opId, String opType,
			String sampleType, String opName, boolean succ, long time, long xferTime,
			long bytes) {
		this.timestamp = timestamp;
		this.succ = succ;
		this.time = time;
		this.xferTime = xferTime;
		this.bytes = bytes;
		this.opType = opType;
		this.sampleType = sampleType;
		this.opName = opName;
		this.opId = opId;
	}

    public Date getTimestamp() {
        return timestamp;
    }
    
	public String getOpName() {
		return opName;
	}

	public String getOpId() {
		return opId;
	}

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isSucc() {
        return succ;
    }

    public void setSucc(boolean succ) {
        this.succ = succ;
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

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getXferTime() {
		return xferTime;
	}

    public void setXferTime(long xferTime) {
		this.xferTime = xferTime;
	}

    public long getBytes() {
        return bytes;
    }

    public void setBytes(long bytes) {
        this.bytes = bytes;
    }

}
