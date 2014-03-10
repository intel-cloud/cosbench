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

package com.intel.cosbench.controller.service;

import java.net.InetAddress;

import com.intel.cosbench.model.DriverInfo;

public class PingDriverRunner implements Runnable{

	private int interval = 5000;
	private DriverInfo[] driverInfos;
	
	PingDriverRunner(DriverInfo[] driverInfos){
		this.driverInfos = driverInfos;
	}
	
	@Override
	public void run() {
		while (true) {
			pingDrivers(driverInfos);
			try {
				Thread.sleep(interval);
			} catch (InterruptedException ignore) {
			}
		}
	}

	private void pingDrivers(DriverInfo[] driverInfos) {
		for (DriverInfo driver : driverInfos) {
			boolean isAlive = false;
			
			String ipAddress = getIpAddres(driver.getUrl());
			try {
				if (!ipAddress.isEmpty()) {
					isAlive = InetAddress.getByName(ipAddress).isReachable(3000);				
				}
			} catch (Exception ignore) {
			}
			driver.setAliveState(isAlive);
		}
	}
	
	private String getIpAddres(String url) {
		int start = url.indexOf('/') + 2;
		int end = url.lastIndexOf(':');		
		return end > start ? url.substring(start, end) : null;
	}
	
}

