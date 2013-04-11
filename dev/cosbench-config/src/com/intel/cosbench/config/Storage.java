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
 * The model class mapping to "storage" in configuration xml with following form:
 * 	<storage type="type" config="config" />
 * 
 * @author ywang19, qzheng7
 *
 */
public class Storage {

    private String type;
    private String config;

    public Storage() {
        /* empty */
    }

    public Storage(String type) {
        setType(type);
    }

    public Storage(String type, String config) {
        setType(type);
        setConfig(config);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        if (StringUtils.isEmpty(type))
            throw new ConfigException("storage type cannot be empty");
        this.type = type;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        /* configuration might be empty */
        this.config = config;
    }

    public void validate() {
        setType(getType());
    }

}
