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

import java.util.HashMap;
import java.util.Map;

import com.intel.cosbench.log.Logger;

public class ErrorStatistics {
	private HashMap<String, Exception> stackTraceAndException;
	private HashMap<String, String> stackTraceAndTargets;
	
	public ErrorStatistics(){
		stackTraceAndException = new HashMap<String, Exception>();
		stackTraceAndTargets = new HashMap<String, String>(); 
	}


	public HashMap<String, Exception> getStackTraceAndException() {
		return stackTraceAndException;
	}

	public HashMap<String, String> getStackTraceAndTargets() {
		return stackTraceAndTargets;
	}

	public void summaryToMission(Logger logger){
		Exception e = null;
		String message = null;
		String code = null;
		Integer codeNumber;
		for(Map.Entry<String, String> entry : stackTraceAndTargets.entrySet()){
			e = stackTraceAndException.get(entry.getKey());
			if (e != null)
				message = e.getMessage();
			if (message != null)
				code = message.substring(9, 12);
			codeNumber = getCodeNumber(entry.getValue());	
			logger.error("error code: " + code + " occurred " +codeNumber + " times, fail to operate: " + entry.getValue(), stackTraceAndException.get(entry.getKey()));			
		}
	}
	
	public HashMap<String, Integer> summaryToResponse(){
		HashMap<String, Integer> codeAndNumber = new HashMap<String, Integer>();
		Exception e = null;
		String message = null;
		String code = null;
		Integer codeNumber;
		for(Map.Entry<String, String> entry : stackTraceAndTargets.entrySet()){
			e = stackTraceAndException.get(entry.getKey());
			if (e != null)
				message = e.getMessage();
			if (message != null)
				code = message.substring(9, 12);
			codeNumber = getCodeNumber(entry.getValue());
			codeAndNumber.put(code, codeNumber);
		}
		return codeAndNumber;
	}
	
	public Integer getCodeNumber(String targets){
		if (targets == null)
			return null;
		int codeNumber = targets.split(",").length;
		return codeNumber;
	}
	
	
	
	
	
}
