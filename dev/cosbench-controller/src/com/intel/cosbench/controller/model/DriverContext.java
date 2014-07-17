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

package com.intel.cosbench.controller.model;

import java.util.HashMap;
import java.util.Map;

import com.intel.cosbench.model.DriverInfo;
import com.intel.cosbench.utils.MapRegistry;

/**
 * This class encapsulates the driver sections in controller.conf.
 * 
 * @author ywang19, qzheng7
 * 
 */
public class DriverContext implements DriverInfo, MapRegistry.Item {

    private String name;
    private String url;
    private boolean aliveState;
    private Map<String, String> pIDMap = new HashMap<String, String>();

	public DriverContext() {
        /* empty */
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void setAliveState(boolean aliveState) {
        this.aliveState = aliveState;
    }

    @Override
    public boolean getAliveState(){
    	return aliveState;
    }


	public String getPIDMap(String scriptName) {
		String pid = pIDMap.remove(scriptName);		
		return (pid == null) ? "0" : pid;
	}

	public void putPIDMap(String scriptName, String pid) {
		if (pid == null)
			pIDMap.put(scriptName, "0");
		pIDMap.put(scriptName, pid);
	}
	
}
