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

package com.intel.cosbench.config;

import org.apache.commons.lang.StringUtils;

/**
 * The model class mapping to "operation" in configuration xml with following form:
 * 	<operation type="type" ratio="ratio" division="division" config="config" />
 * 
 * @author ywang19, qzheng7
 *
 */
public class Operation {

    private String type;
    private int ratio = 100;
    private String division;
    private String config = "";
    private String id = "none"; /* will be inited in workloadProcessor.initStageOpId() */

    public Operation() {
        /* empty */
    }

    public Operation(String type) {
        setType(type);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        if (StringUtils.isEmpty(type))
            throw new ConfigException("operation type cannot be empty");
        this.type = type;
    }

    public int getRatio() {
        return ratio;
    }

    public void setRatio(int ratio) {
        if (ratio < 0 || ratio > 100)
            /* ratio can be set to zero */
            throw new ConfigException("illegal operation ratio: " + ratio);
        this.ratio = ratio;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        if (StringUtils.isEmpty(division))
            throw new ConfigException("oepration must have its division");
        this.division = division;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        /* configuration might be empty */
        this.config = config;
    }
    
    
    public String getId() {
    	return id;
    }
    
    public void setId(String id) {
    	this.id = id;
    }


    public void validate() {
        setType(getType());
        setDivision(getDivision());
    }

}
