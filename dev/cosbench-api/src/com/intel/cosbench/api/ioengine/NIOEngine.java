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

package com.intel.cosbench.api.ioengine;

import static com.intel.cosbench.api.ioengine.IOEngineConstants.*;

import com.intel.cosbench.api.context.*;
import com.intel.cosbench.config.Config;
import com.intel.cosbench.log.Logger;

/**
 * This class encapsulates one none authentication mechanism which is used if no
 * any other authentication mechanism is assigned.
 * 
 * @author ywang19
 * 
 */
public class NIOEngine implements IOEngineAPI {

    public static final String API_TYPE = "none";

    protected Context parms;
    protected Logger logger;

    /* configurations */
    private boolean logging; // enable logging

    public NIOEngine() {
        /* empty */
    }

    @Override
    public boolean init(Config config, Logger logger) {
        this.logger = logger;
        this.parms = new Context();
        
        logging = config.getBoolean(LOGGING_KEY, LOGGING_DEFAULT);
        /* register all parameters */
        parms.put(LOGGING_KEY, logging);
        
        return true;
    }

    @Override
    public void dispose() {
        /* empty */
    }

    @Override
    public Context getParms() {
        return parms;
    }

    @Override
    public IOEngineContext startup() {
        if (logging)
            logger.info("start up io engine : " + API_TYPE);
        return null;
    }

    @Override
    public boolean shutdown() {
        if (logging)
            logger.info("shut down io engine : " + API_TYPE);
        return true;
    }
}
