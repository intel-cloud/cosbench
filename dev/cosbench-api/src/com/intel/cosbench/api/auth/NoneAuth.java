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

package com.intel.cosbench.api.auth;

import static com.intel.cosbench.api.auth.AuthConstants.*;

import com.intel.cosbench.api.context.*;
import com.intel.cosbench.config.Config;
import com.intel.cosbench.log.Logger;

/**
 * This class encapsulates one none authentication mechanism which is used if no
 * any other authentication mechanism is assigned.
 * 
 * @author ywang19, qzheng7
 * 
 */
public class NoneAuth implements AuthAPI {

    public static final String API_TYPE = "none";

    protected AuthContext parms;
    protected Logger logger;

    /* configurations */
    private boolean logging; // enable logging
    private boolean caching; // enable caching

    public NoneAuth() {
        /* empty */
    }

    @Override
    public void init(Config config, Logger logger) {
        this.logger = logger;
        this.parms = new DefaultAuthContext();
        
        logging = config.getBoolean(LOGGING_KEY, LOGGING_DEFAULT);
        caching = config.getBoolean(CACHING_KEY, CACHING_DEFAULT);
        
        /* register all parameters */
        parms.put(LOGGING_KEY, logging);
        parms.put(CACHING_KEY, caching);
    }
    @Override
    public void init() {
    	/* empty */
    }
    

    @Override
    public void dispose() {
        /* empty */
    }

    @Override
    public AuthContext getParms() {
        return parms;
    }

    @Override
    public AuthContext login() {
        if (logging)
            logger.info("perform authentication");
        return null;
    }

}
