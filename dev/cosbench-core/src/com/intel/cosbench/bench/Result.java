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
 * This class encapsulates the returned result for one operation.
 * 
 * @author ywang19, qzheng7
 *
 */
public class Result {

    private Date timestamp;

    private boolean succ;
    private String opType;
    private String sampleType;
    private String opName;
    private String opId;

	public Result(Date timestamp, String opId, String opType,
			String sampleType, String opName, boolean succ) {
		this.timestamp = timestamp;
		this.succ = succ;
		this.opType = opType;
		this.sampleType = sampleType;
		this.opName = opName;
		this.opId = opId;
	}

    public Date getTimestamp() {
        return timestamp;
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
    
    public String getOpName(){
    	return opName;
    }
    
    public String getOpId() {
    	return opId;
    }

}
