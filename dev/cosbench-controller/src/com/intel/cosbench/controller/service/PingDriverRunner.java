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

	private static final int base_interval = 20;	// heartbeat interval in seconds.
	private static final int failure_tolerance = 3; // how many failures in a row is tolerable.
	private static final int ext_interval = 3*base_interval; // extended interval for the case failures is over tolerance.
	private static final int delay = 3; // timeout for socket wait in seconds
	private DriverInfo[] driverInfos;
	private int driver_count = 0;
	private int[] failures;
	private boolean[] all_alive;
	
	PingDriverRunner(DriverInfo[] driverInfos){
		this.driverInfos = driverInfos;
		this.driver_count = driverInfos.length;
		this.failures = new int[this.driver_count];
		this.all_alive = new boolean[this.driver_count];
	}
	
	public boolean isAlive() {
		for(boolean status : all_alive) {
			if(!status)
				return false;
		}
		
		return true;
	}
	
	public void checkFaultyDrivers() {
		for(int i=0; i<driver_count; i++) {
			if(!all_alive[i]) {
				LOGGER.error(driverInfos[i].getName() + " at " + driverInfos[i].getUrl());
			}
		}		
	}
	
	@Override
	public void run() {
		while (true) {
			pingDrivers(driverInfos);
			int interval = base_interval;
			if(!isAlive()) {
				LOGGER.error("below drivers are not alive:");
				checkFaultyDrivers();
				interval = ext_interval;
			}

			try {
				LOGGER.info("begin to sleep for " + interval + " seconds.");
				Thread.sleep(interval*1000);
				LOGGER.info("wake up from sleep.");				
			} catch (InterruptedException ie) {
				LOGGER.warn("thread sleep is interrupted.");
			}
		}
	}

	private void pingDrivers(DriverInfo[] driverInfos) {

		for (int i=0; i<driverInfos.length; i++) {
			DriverInfo driver = driverInfos[i];
			String ipAddress = getIpAddres(driver.getUrl());
			Integer port = getDriverPort(driver.getUrl());
			LOGGER.info("Trying to ping driver " + driver.getName() + " at " + driver.getUrl() + " with ip=" + ipAddress + ", port=" + port + "...");
			if (ipAddress == null || ipAddress.isEmpty()) { 
				all_alive[i] = false;
				failures[i] = failure_tolerance;
				LOGGER.error("the driver ip address shouldn't be empty, the heartbeat is disabled to driver !" + driver.getName());
				return;
			}
			
			Socket socket = null;
			try{
				socket = new Socket();
				socket.connect(new InetSocketAddress(ipAddress, port), delay*1000);
				all_alive[i] = true;
				failures[i] = 0;
				LOGGER.info("The driver " + driver.getName() + " at " + driver.getUrl() + " is reachable");
			}catch(Exception e){
				failures[i]++;
				LOGGER.warn("The driver " + driver.getName() + " at " + driver.getUrl() + " is not reachable at the " + failures[i] + " time, with error message: " + e.getMessage());
				if(failures[i] >= failure_tolerance) {
					all_alive[i] = false;
					LOGGER.warn("The driver " + driver.getName() + " at " + driver.getUrl() + " hits failure tolerance, will extend the heartbeat interval to " + ext_interval + " seconds");
				}
			}finally{
				driver.setAliveState(all_alive[i]);
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

