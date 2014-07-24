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

package com.intel.cosbench.driver.handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import com.intel.cosbench.protocol.Response;
import com.intel.cosbench.protocol.TriggerResponse;
import com.intel.cosbench.service.DriverService;

public class TriggerHandler extends AbstractCommandHandler {
	
	private int currPID = 0;
	private int xferPID = 0;
	private boolean isEnable = false;
	protected static String scriptsDir = "scripts/";
	protected DriverService driver;
	protected String scriptLog;
	
	
    public void setDriver(DriverService driver) {
        this.driver = driver;
    }
    
    @Override
    protected Response process(HttpServletRequest req, HttpServletResponse res)
            throws Exception {
    	scriptLog = "";
    	Scanner scanner = new Scanner(req.getInputStream());
    	String trigger = getTrigger(scanner);
    	runTrigger(trigger);
    	
        return createResponse();
    }
    
    private String getTrigger(Scanner scanner) {
    	if (!scanner.hasNext())
            LOGGER.error("bad request exception");
    	String trigger = scanner.next();
    	if (trigger == null)
    		LOGGER.error("no found exception");
        return trigger;
	}
    
    private void runTrigger(String trigger) {
		String[] cmdArr = getCmdArray(trigger);
		if (cmdArr == null) {
			return;
		}
		LOGGER.info("executing trigger command line : {} on {}", cmdArr, getDriverName());
		String osType = System.getProperty("os.name").toLowerCase();
    	if (osType.contains("linux")) {
    		try {
    			Process process = Runtime.getRuntime().exec(cmdArr);
    			getPID(process);
    			InputStream is = process.getInputStream();
   	         	BufferedReader buff = new BufferedReader(new InputStreamReader(is));  
   	         	String line = null;
   	         	while ((line = buff.readLine()) != null)
   	         		scriptLog += line + "\n";
   	         	process.waitFor();
			} catch (Exception e) {
				LOGGER.error("execute trigger command failed!");
				return;
			}
		} else {
			LOGGER.warn("can not execute trigger on {}, the OS type({}) isn't linux!", getDriverName(), osType);
			return;
		}
    	if (!isEnable) {
			if (!isTriggerKilled(currPID)) {
				killByPID(currPID);
			}
			if (!isTriggerKilled(xferPID))
				killByPID(xferPID);
		}
	}
    
    private String[] getCmdArray(String trigger) {
    	trigger.replace(" ", "");
    	String[] triggerArr = StringUtils.split(trigger, ',');
    	if (triggerArr == null) {
    		LOGGER.warn("trigger command array is empty!");
			return null;
		}
    	if (triggerArr[0].equals("killTrigger")) {
    		this.isEnable = false;
    		if (triggerArr.length != 3) {
    			LOGGER.error("kill-trigger command line is illegal!");
				return null;
			}
    		xferPID = parsePID(triggerArr[1]);
    		String fileName = triggerArr[2];
    		String filePath = scriptsDir + fileName;
        	File tempPath = new File(filePath);
        	if (!tempPath.exists() || !tempPath.isFile()) {
    			LOGGER.warn("trigger file {} dosen't exist on {}!", filePath, getDriverName());
    			return null;
        	}
    		return new String[]{"/bin/sh", filePath, "-k"};
		} else if (triggerArr[0].equals("enableTrigger")) {
			this.isEnable = true;
			String fileName = triggerArr[1];
			String filePath = scriptsDir + fileName;
	    	File tempPath = new File(filePath);
	    	if (!tempPath.exists() || !tempPath.isFile()) {
				LOGGER.warn("trigger file {} dosen't exist on {}!", filePath, getDriverName());
				return null;
	    	}
			String[] cmdArr = new String[triggerArr.length];
			cmdArr[0] = "/bin/sh";
			cmdArr[1] = filePath;
			for (int i = 2; i < triggerArr.length; i++) {
				cmdArr[i] = triggerArr[i];
			}
			return cmdArr;
		} else {
			LOGGER.error("trigger command line is illegal!");
			return null;
		}
	}
    
    private int parsePID(String str) {
		int num = 0;
    	try {
    		num = Integer.parseInt(str);
		} catch (NumberFormatException e) {
			LOGGER.error("can not parse PID from String to Int {}!", str);
		}
		return num;
	}
    
    private void getPID(Process process) {
		Field field = null;
		if (!process.getClass().getName().equals("java.lang.UNIXProcess")) {
			LOGGER.error("failed to get PID by {}", process.getClass().getName());
			return;
		}
		try {
			field = process.getClass().getDeclaredField("pid");
			field.setAccessible(true);
			currPID = (Integer) field.get(process);
		} catch (Exception e) {
			LOGGER.error("get PID failed!");
		}
		if (isEnable)
			xferPID = currPID;
		LOGGER.debug("current PID is: {}", currPID);
	}
    
    private boolean isTriggerKilled(int pid) {
    	List<Integer> pids = getAllPID(pid);
    	if (pids != null && pids.size() >= 1)
			return false;
		return true;
	}

    private void killByPID(int pid) {
    	List<Integer> pidList = getAllPID(pid);
    	if (pidList == null || pidList.isEmpty()) {
			//LOGGER.debug("pid {} have been killed", pid);
    		return;
		}
    	if (pidList.size() == 1) {
			//LOGGER.debug("pid {} have no child process", pid);
		} else {
			//LOGGER.debug("pid {} have child process {}", pid, pidList);
	    	for (int i = 1; i < pidList.size(); i++) {
				killByPID(pidList.get(i));
			}
		}
    	runKill9(pid);
	}

    private List<Integer> getAllPID(int pid) {
    	if (pid <= 0) {
    		LOGGER.warn("pid is illegal: {}", pid);
			return null;
    	}
		List<Integer> pidList = new ArrayList<Integer>();
		String cmdLine = "ps -ef | grep " + pid + " | awk '{print $2}'";
		try {
	         Process process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", cmdLine}); 
	         InputStream is = process.getInputStream();
	         BufferedReader buff = new BufferedReader(new InputStreamReader(is));  

	         String line = null;
	         while ((line = buff.readLine()) != null) {
	        	int tmp = 0;
	        	if ((tmp = parsePID(line.trim())) <= 0){
	        		LOGGER.error("buffer line is illegal: {}", line);
					continue;
	        	}
	        	//LOGGER.debug("child pid of {} : {}", pid, tmp);
	            pidList.add(new Integer(tmp));
	         }
	         process.waitFor();
		} catch (Exception e) {
			LOGGER.error("get all PID failed!");
			return null;
		}
		if (pidList == null || pidList.isEmpty())
			return null;
		pidList.remove(pidList.size()-1);
		pidList.remove(pidList.size()-1);
		return pidList;
	}

    private void runKill9(int pid) {
    	String osType = System.getProperty("os.name").toLowerCase();
		if (osType.contains("linux")) {
    		try {
    			Runtime.getRuntime().exec(new String[]{"kill","-9",Integer.toString(pid)});
    			LOGGER.debug("pid {} have been killed directly", pid);
			} catch (Exception e) {
				LOGGER.error("run <kill -9 {}> failed!", pid);
			}
		} else {
			LOGGER.warn("can not run kill command on {}, the OS type({}) isn't linux!", getDriverName(), osType);
		}
	}
    
    private String getDriverName() {
		String name = driver.getDriverInfo().getName();
		if ( name != null && !name.isEmpty())
			return name;
		try {
			name = InetAddress.getLocalHost().getHostName();
		} catch (Exception e) {
			LOGGER.warn("can not get local hostname on {}", driver.getDriverInfo().getUrl());
		}
		return name;
	}
    
    private Response createResponse() {
        TriggerResponse response = new TriggerResponse();
        response.setPID(isEnable ? Integer.toString(xferPID) : "0");
        response.setScriptLog(scriptLog);
        return response;
    }   

}
