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

import java.util.*;

import com.intel.cosbench.config.common.ConfigUtils;
import com.intel.cosbench.log.LogFactory;
import com.intel.cosbench.log.Logger;

/**
 * The model class mapping to "workflow" in configuration xml with following form:
 * 	<workflow />
 * 
 * @author ywang19, qzheng7
 *
 */
public class Workflow implements Iterable<Stage> {

    private List<Stage> stages;
    private String config = "";

    public Workflow() {
        /* empty */
    }

    public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}

	public List<Stage> getStages() {
        return stages;
    }

    public void setStages(List<Stage> stages) {
        if (stages == null || stages.isEmpty())
            throw new ConfigException("workflow must have stages");
        for(Stage stage: stages) {
        	stage.setConfig(ConfigUtils.inherit(stage.getConfig(), this.config));
        }
        this.stages = stages;
    }

    public void addStage(Stage stage) {
        if (stage == null || stage.getName().isEmpty())
            throw new ConfigException("can't add one empty stage");
        if (stages == null)
            stages = new ArrayList<Stage>();
        stage.setConfig(ConfigUtils.inherit(stage.getConfig(), this.config));
        stages.add(stage);
    }

    @Override
    public Iterator<Stage> iterator() {
    	if(stages == null){
    		stages = new ArrayList();
    	}
    	return stages.iterator();
    }

    public void validate() {
        setStages(getStages());
        for (Stage stage : stages)
            stage.validate();
    }

}
