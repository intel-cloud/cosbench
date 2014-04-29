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

import com.intel.cosbench.config.Config;
import com.intel.cosbench.log.*;

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

}
