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
import java.util.regex.Pattern;

import javax.lang.model.element.Element;

import com.intel.cosbench.log.Logger;

public class ErrorStatistics {
	private HashMap<String, Exception> stackTraceAndException;
	private HashMap<String, String> stackTraceAndTargets;
	
	//summary the result 
	private HashMap<String, String> stackTraceAndMessage;
	private HashMap<String, String> stackTraceAndErrorCode;
	private HashMap<String, String> stackTraceAndNum;
	private HashMap<String, String> stackTraceAndEntireTrace;
	private HashMap<String, Integer> errorCodeAndNum;
	
	
	public ErrorStatistics(){
		stackTraceAndException = new HashMap<String, Exception>();
		stackTraceAndTargets = new HashMap<String, String>(); 
		stackTraceAndMessage = new HashMap<String, String>();
		stackTraceAndErrorCode = new HashMap<String, String>();
		stackTraceAndEntireTrace = new HashMap<String, String>();
		stackTraceAndNum = new HashMap<String, String>();
		errorCodeAndNum = new HashMap<String, Integer>();
	}


	public HashMap<String, Exception> getStackTraceAndException() {
		return stackTraceAndException;
	}

	public HashMap<String, String> getStackTraceAndTargets() {
		return stackTraceAndTargets;
	}
	
	
	public HashMap<String, String> getStackTraceAndMessage() {
		return stackTraceAndMessage;
	}


	public HashMap<String, String> getStackTraceAndErrorCode() {
		return stackTraceAndErrorCode;
	}


	public HashMap<String, String> getStackTraceAndEntireTrace() {
		return stackTraceAndEntireTrace;
	}
	


	public HashMap<String, String> getStackTraceAndNum() {
		return stackTraceAndNum;
	}
	
	public HashMap<String, Integer> getErrorCodeAndNum() {
		return errorCodeAndNum;
	}


	public void summaryToMission(Logger logger){
		Exception e = null;
		String message = null;
		String code = null;
		Integer codeNumber;
		String trace = "";
		for(Map.Entry<String, String> entry : stackTraceAndTargets.entrySet()){
			e = stackTraceAndException.get(entry.getKey());
			if (e != null)
				message = e.getMessage();
			stackTraceAndMessage.put(entry.getKey(), message);
			if (message != null)
				code = message.substring(9, 12);
			if(!isInteger(code))
				code = "N/A";
			stackTraceAndErrorCode.put(entry.getKey(), code);
			codeNumber = getCodeNumber(entry.getValue());
			stackTraceAndNum.put(entry.getKey(), String.valueOf(codeNumber));
			if(errorCodeAndNum.containsKey(code)){
				Integer num = errorCodeAndNum.get(code);
				errorCodeAndNum.put(code, num + codeNumber);
			}
			else{
				errorCodeAndNum.put(code, codeNumber);
			}
			for(StackTraceElement ele: stackTraceAndException.get(entry.getKey()).getStackTrace())
				trace += ele.toString()+"\n";
			stackTraceAndEntireTrace.put(entry.getKey(), trace);
			logger.error("error code: " + code + " occurred " +codeNumber + " times, fail to operate: " + entry.getValue(), stackTraceAndException.get(entry.getKey()));			
		}
	}
	
	public Integer getCodeNumber(String targets){
		if (targets == null)
			return null;
		int codeNumber = targets.split(",").length;
		return codeNumber;
	}
	 	  
	  public static boolean isInteger(String str) {    
	    Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");    
	    if(str !=null && pattern.matcher(str) != null)
	    	return pattern.matcher(str).matches();
	    else
	    	return false;
	  }  

}
