package com.intel.cosbench.controller.model;

import java.util.HashMap;

public class ErrorSummary {
	private HashMap<String, Integer> errorCodeAndNum;
	public ErrorSummary(){
		errorCodeAndNum = new HashMap<String, Integer>();
	}
	public ErrorSummary(HashMap<String, Integer> errors){
		errorCodeAndNum = errors;
	}
	public HashMap<String, Integer> getErrorCodeAndNum() {
		return errorCodeAndNum;
	}
	public void assignEntry(String key, Integer value){
		errorCodeAndNum.put(key, value);
	}
	public boolean contains(String key){
		return errorCodeAndNum.containsKey(key);
	}
	

}
