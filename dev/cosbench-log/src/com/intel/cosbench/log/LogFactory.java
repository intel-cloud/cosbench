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

package com.intel.cosbench.log;

/**
 * A Factory class to create different LogManager based on log name, by default
 * log4j LogManager will use.
 * 
 * @author ywang19, qzheng7
 * 
 */
public class LogFactory {

    public static final String DEFAULT_LOGMGR = "com.intel.cosbench.log.log4j.Log4jLogManager";

    private static final LogManager SYS_MGR = new com.intel.cosbench.log.log4j.Log4jLogManager();

    public static LogManager createLogManager() {
        return createLogManager(DEFAULT_LOGMGR);
    }

    /**
     * The method creates one LogManager instance by provided class name.
     * 
     * @param logmgr
     *            the class name for log manager
     * @return one new LogManager instance created by logmgr name, if any
     *         exceptions, will return default log4j LogManager.
     */
    public static LogManager createLogManager(String logmgr) {
        try {
            return (LogManager) Class.forName(logmgr).newInstance();
        } catch (Exception e) {
            System.out
                    .println("Can't initiaze specified LogManager, use default log4j instead.");
            e.printStackTrace();
        }

        return SYS_MGR;
    }

    public static Logger getSystemLogger() {
        return SYS_MGR.getLogger();
    }

    public static LogManager getSystemLogManager() {
        return SYS_MGR;
    }

}
