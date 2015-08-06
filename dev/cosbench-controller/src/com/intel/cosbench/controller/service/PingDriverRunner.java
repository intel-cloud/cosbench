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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.intel.cosbench.log.LogFactory;
import com.intel.cosbench.log.Logger;
import com.intel.cosbench.model.DriverInfo;

public class PingDriverRunner implements Runnable{

    protected static final Logger LOGGER = LogFactory.getSystemLogger();
	private DriverInfo[] driverInfos;
	private int interval = 15*000;	// set longer interval.
	private int failure_tolerance = 20; // in total 5 mins.
	private int[] failures;
	private boolean isAlive = true;
	
	PingDriverRunner(DriverInfo[] driverInfos){
		this.driverInfos = driverInfos;
		this.failures = new int[this.driverInfos.length];
	}
	
	@Override
	public void run() {
		while (isAlive) {
			pingDrivers(driverInfos);
			try {
				Thread.sleep(interval);
			} catch (InterruptedException ignore) {
			}
		}
	}

	private void pingDrivers(DriverInfo[] driverInfos) {

		for (int i=0; i<driverInfos.length; i++) {
			DriverInfo driver = driverInfos[i];
			LOGGER.info("Trying to ping driver " + driver.getName() + " at " + driver.getUrl());
			String ipAddress = getIpAddres(driver.getUrl());
			Integer port = getDriverPort(driver.getUrl());
			if (ipAddress == null || ipAddress.isEmpty()) { 
				isAlive = false;
				failures[i] = failure_tolerance;
				LOGGER.error("the driver ip address shouldn't be empty, the heartbeat is disabled to driver !" + driver.getName());
				return;
			}

			Socket socket = null;
			try{
				socket = new Socket();
				InetSocketAddress reAddress = new InetSocketAddress(ipAddress, port);
				InetSocketAddress locAddress = new InetSocketAddress("0.0.0.0", 0);
				socket.bind(locAddress);
				socket.connect(reAddress,3000);
				isAlive = true;
				failures[i] = 0;
				LOGGER.info("The driver " + driver.getName() + " at " + driver.getUrl() + " is reachable");
			}catch(Exception e){
				failures[i]++;
				LOGGER.warn("The driver " + driver.getName() + " at " + driver.getUrl() + " is not reachable at the " + (i+1) + " time, with below exception: \n" + e.getMessage());
				if(failures[i] >= failure_tolerance) {
					isAlive = false;
					LOGGER.warn("The driver " + driver.getName() + " at " + driver.getUrl() + " hits failure tolerance, will disable the heartbeat to it");
				}
			}finally{
				driver.setAliveState(isAlive);
				if (socket != null) {
					try {
						socket.close();
					}catch (IOException ignore) {						
					}
				}
			}

		}
	}
	
	private String getIpAddres(String url) {
		int start = url.indexOf('/') + 2;
		int end = url.lastIndexOf(':');		
		return end > start ? url.substring(start, end) : null;
	}
	
	private Integer getDriverPort(String url) {
		int start = url.lastIndexOf(":")+1;
		int end = url.lastIndexOf("/");
		return end > start? Integer.valueOf(url.substring(start, end)):null;
	}

}

