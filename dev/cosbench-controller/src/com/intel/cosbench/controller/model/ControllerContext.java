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

package com.intel.cosbench.controller.model;

import com.intel.cosbench.model.*;

/**
 * This class encapsulates the configurations in controller.conf.
 * 
 * @author ywang19, qzheng7
 * 
 */
public class ControllerContext implements ControllerInfo {

    private String name;
    private String url;
    private int concurrency;
    private DriverRegistry driverRegistry;
    private String userName;
    private String passWord;
    private boolean needAuth;

    public ControllerContext() {
        /* empty */
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getConcurrency() {
        return concurrency;
    }

    public void setConcurrency(int concurrency) {
        this.concurrency = concurrency;
    }

    public DriverRegistry getDriverRegistry() {
        return driverRegistry;
    }

    public void setDriverRegistry(DriverRegistry driverRegistry) {
        this.driverRegistry = driverRegistry;
    }

    @Override
    public int getDriverCount() {
        return driverRegistry.getSize();
    }

    @Override
    public DriverInfo[] getDriverInfos() {
        return driverRegistry.getAllDrivers();
    }
    
    public String getUserName(){
    	return userName;
    }
    
    public void setUserName(String userName){
    	this.userName = userName;
    }
    
    public String getPassWord(){
    	return passWord;
    }
    
    public void setPassWord(String passWord){
    	this.passWord = passWord;
    }
    
    public boolean getNeddAuth(){
    	return needAuth;
    }
    
    public void setNeedAuth(boolean needAuth){
    	this.needAuth = needAuth;
    }

}
