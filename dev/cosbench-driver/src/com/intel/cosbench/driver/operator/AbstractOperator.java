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

package com.intel.cosbench.driver.operator;


import java.util.HashMap;

import com.intel.cosbench.bench.ErrorStatistics;
import com.intel.cosbench.config.Config;
import com.intel.cosbench.log.LogFactory;
import com.intel.cosbench.log.Logger;

/**
 * The base class encapsulates different operations.
 * 
 * @author ywang19, qzheng7
 * 
 */
abstract class AbstractOperator implements Operator {

    private static final Logger LOGGER = LogFactory.getSystemLogger();

    protected Config config;
    protected String id;
    protected String name;
    protected int ratio;

    @Override
    public String getName() {
        return name;
    }

    protected void init(String id, int ratio, String division, Config config) {
        this.config = config;
        this.id = id;
        this.ratio = ratio;
		name = config.get("name", getOpType());
    }
    
    abstract public String getOpType();
    
    @Override
    public int getRatio() {
    	return ratio;
    }
    
    @Override
    public String getSampleType() {
        return getOpType();
    }
    
    @Override
    public String getId(){
    	return id;
    }

    @Override
    public void operate(Session session) {
        int idx = session.getIndex();
        int all = session.getTotalWorkers();
        operate(idx, all, session);
    }
    
    protected static void doLogInfo(Logger logger, String message) {
        if (logger != null)
            logger.info(message);
        else
            AbstractOperator.LOGGER.info(message);
    }
    
    protected static void doLogDebug(Logger logger, String message) {
        if (logger != null)
            logger.debug(message);
        else
            AbstractOperator.LOGGER.debug(message);
    }

    protected static void doLogWarn(Logger logger, String message) {
        if (logger != null)
            logger.warn(message);
        else
            AbstractOperator.LOGGER.warn(message);
    }

    protected static void doLogErr(Logger logger, String message) {
        if (logger != null)
            logger.error(message);
        else
            AbstractOperator.LOGGER.error(message);
    }

    protected static void doLogErr(Logger logger, String message, Exception e) {
        if (logger != null)
            logger.error(message, e);
        else
            AbstractOperator.LOGGER.error(message, e);
    }

    protected abstract void operate(int idx, int all, Session session);
    
    public static void errorStatisticsHandle(Exception e, Session session, String target){
    		String trace = e.getStackTrace()[0].toString();
    		trace = e.getCause() == null ? trace : trace + e.getCause().getStackTrace()[0].toString();
    		ErrorStatistics errorStatistics = session.getErrorStatistics();
    		HashMap<String, String> stackTraceAndTargets = errorStatistics.getStackTraceAndTargets();
    		synchronized (stackTraceAndTargets) {
    			if(! stackTraceAndTargets.containsKey(trace)){
    				errorStatistics.getStackTraceAndException().put(trace, e);
    				stackTraceAndTargets.put(trace, target);
    				doLogErr(session.getLogger(), "worker "+ session.getIndex() + " fail to perform operation " + target, e);
    			}
    			String targets = stackTraceAndTargets.get(trace);
    			stackTraceAndTargets.put(trace, targets + ", "+target);
    		}
    }
    public static void isUnauthorizedException(Exception e, Session session) {
    	if(e != null && e.getMessage() != null)
    		try{
    			if(401 == Integer.valueOf(e.getMessage().substring(9, 12))){
    				session.getApi().setAuthFlag(false);
    				LOGGER.debug("catch 401 error from storage backend, set auth flag to false");
    			}
    		}catch(NumberFormatException ne) {
    			//ne.printStackTrace();// mask ignore
    		}
    }

}
