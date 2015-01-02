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

import com.intel.cosbench.config.common.ConfigUtils;
import com.intel.cosbench.log.LogFactory;
import com.intel.cosbench.log.Logger;

/**
 * The model class mapping to "workload" in configuration xml with following form:
 * 	<workload name="name" description="desc" />
 * 
 * @author ywang19, qzheng7
 *
 */
public class Workload {

    private static final Auth DEFAULT_AUTH = new Auth("none");
    private static final Storage DEFAULT_STORAGE = new Storage("none");

    private String name;
    private String description;
    private String trigger=null;
    private String config = "";
    private Auth auth = DEFAULT_AUTH;
    private Storage storage = DEFAULT_STORAGE;
    private Workflow workflow;

    public Workload() {
        /* empty */
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (StringUtils.isEmpty(name))
            throw new ConfigException("workload name cannot be empty");
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        /* description might be empty */
        this.description = description;
    }
    
    public String getTrigger() {
		return trigger;
	}

	public void setTrigger(String trigger) {
		this.trigger = trigger;
	}
	
    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        /* configuration might be empty */
        this.config = config;
    }
    
    public Auth getAuth() {
        return auth;
    }

    public void setAuth(Auth auth) {
        if (auth == null)
            throw new ConfigException("workload must have a default auth");
        this.auth = auth;
    }

    public Storage getStorage() {
        return storage;
    }

    public void setStorage(Storage storage) {
        if (storage == null)
            throw new ConfigException("workload must have a default storage");
        this.storage = storage;
    }

    public Workflow getWorkflow() {
        return workflow;
    }

    public void setWorkflow(Workflow workflow) {
        if (workflow == null)
            throw new ConfigException("workload must have its workflow");
        workflow.setConfig(ConfigUtils.inherit(workflow.getConfig(), this.config));
        this.workflow = workflow;
    }

    public void validate() {
        setName(getName());
        setWorkflow(getWorkflow());
        for (Stage stage : workflow)
            if (stage.getAuth() == null)
                stage.setAuth(auth);
        for (Stage stage : workflow)
            if (stage.getStorage() == null)
                stage.setStorage(storage);
        workflow.validate();
    }

}
