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

package com.intel.cosbench.driver.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Date;

import com.intel.cosbench.model.DriverInfo;

/**
 * This class encapsulates the configurations in driver.conf.
 * 
 * @author ywang19, qzheng7
 * 
 */
public class DriverContext implements DriverInfo {

    private String name;
    private String url;
    private boolean aliveState;
    private String version;
    private String time;

	// pIDMap<scriptName, pid>
	private Map<String, String> pidMap = new HashMap<String, String>();
	// logMap<'wId'+'sId', ScriptLog>
	private Map<String, String> scriptsLog = new HashMap<String, String>();
	
	public String getTime() {
		time = new Date().toString();
		return time;
	}
	
	public void setTime(String timeStr) {
		this.time = timeStr;
	}
	
    public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

    public DriverContext() {
        /* empty */
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setAliveState(boolean aliveState) {
		this.aliveState = aliveState;
	}

    public boolean getAliveState(){
    	return aliveState;
    }

	public String getPidMap(String scriptName) {
		String pid = pidMap.remove(scriptName);		
		return (pid == null) ? "0" : pid;
	}

	public void putPidMap(String scriptName, String pid) {
		if (pid == null)
			pidMap.put(scriptName, "0");
		pidMap.put(scriptName, pid);
	}

	@Override
	public Map<String, String> getLogMap() {
		return scriptsLog;
	}

	public void putLogMap(String wsId, String ScriptLog) {
		if (wsId == null || wsId.isEmpty())
			return;
		scriptsLog.put(wsId, ScriptLog);
	}
	
	public String getLogMapValue(String wsId) {
		return scriptsLog.remove(wsId);
	}
}
