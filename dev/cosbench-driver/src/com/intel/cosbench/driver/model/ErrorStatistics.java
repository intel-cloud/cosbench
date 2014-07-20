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
import java.util.concurrent.ConcurrentHashMap;

import com.intel.cosbench.log.Logger;


public class ErrorStatistics {
	private ConcurrentHashMap<String, Exception> messageAndExceptionStack;
	private HashMap<String, String> messageAndTargets;
	
	public ErrorStatistics(){
		messageAndExceptionStack = new ConcurrentHashMap<String, Exception>();
		messageAndTargets = new HashMap<String, String>();
	}
	public  void addMessageAndTargets(String message,String target){
		synchronized (messageAndTargets) {
			if(messageAndTargets.containsKey(message)){
				String targets = messageAndTargets.get(message);
				targets += target;
				messageAndTargets.put(message, targets);
			}
			else{
				messageAndTargets.put(message, target);
			}
		}
	}
	public ConcurrentHashMap<String, Exception> getMessageAndExceptionStack() {
		return messageAndExceptionStack;
	}

	public void setMessageAndExceptionStack(
			ConcurrentHashMap<String, Exception> messageAndExceptionStack) {
		this.messageAndExceptionStack = messageAndExceptionStack;
	}

	public HashMap<String, String> getMessageAndTargets() {
		return messageAndTargets;
	}

	public void setMessageAndTargets(HashMap<String, String> messageAndTargets) {
		this.messageAndTargets = messageAndTargets;
	}
	public void summary(Logger logger) {
		for (Map.Entry<String, Exception> entry : messageAndExceptionStack.entrySet()){
			String targets = messageAndTargets.get(entry.getKey());
			logger.error("fail to operate "+targets, entry.getValue());
		}
		
	}
	
	
}
