package com.intel.cosbench.client.swift;


import org.apache.http.client.HttpClient;

import com.intel.cosbench.client.http.HttpClientUtil;


public class SwiftMultiWorker {
	
	public static void main(String args[]) {
		HttpClient httpClient = HttpClientUtil.createHttpClient(99999);
		SwiftClient client = new SwiftClient(httpClient);
		
		
		
}}
