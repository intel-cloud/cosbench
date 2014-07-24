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


package com.intel.cosbench.protocol;

public class TriggerResponse extends Response{
	private String PID;
	private String scriptLog;

	public TriggerResponse() {
		/*empty*/
	}

	public String getPID() {
		return PID;
	}

	public void setPID(String pID) {
		this.PID = pID;
	}

	public String getScriptLog() {
		return scriptLog;
	}

	public void setScriptLog(String scriptLog) {
		this.scriptLog = scriptLog;
	}

}
