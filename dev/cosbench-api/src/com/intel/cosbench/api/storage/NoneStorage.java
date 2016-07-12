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

package com.intel.cosbench.api.storage;

import static com.intel.cosbench.api.storage.StorageConstants.*;

import java.io.*;
import java.util.*;

import com.intel.cosbench.api.auth.NoneAuth;
import com.intel.cosbench.api.context.*;
import com.intel.cosbench.config.Config;
import com.intel.cosbench.log.Logger;

/**
 * This class encapsulates one none storage system which is used if no any other
 * storage system is assigned.
 * 
 * @author ywang19, qzheng7
 * 
 */
public class NoneStorage implements StorageAPI {

    public static final String API_TYPE = "none";

    protected Context parms;
    protected Logger logger;
    protected Boolean authFlag;
    /* configurations */
    private boolean logging; // enable logging

    public NoneStorage() {
        /* empty */
    }

    @Override
    public void init(Config config, Logger logger) {
        this.logger = logger;
        this.parms = new Context();
		
        logging = config.getBoolean(LOGGING_KEY, LOGGING_DEFAULT);
        /* register all parameters */
        parms.put(LOGGING_KEY, logging);
        authFlag = false;
    }

    @Override
    public void setAuthContext(AuthContext info) {
    	setAuthFlag(true);
        /* empty */
    }

	@Override
	public AuthContext getAuthContext() {
		// TODO Auto-generated method stub
		return null;
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
    public void abort() {
        /* empty */
    }

    @Override
    public InputStream getObject(String container, String object, Config config) {
        if (logging)
            logger.info("performing GET at /{}/{}", container, object);
        return new ByteArrayInputStream(new byte[] {});
    }
    
    @Override
    public InputStream getList(String container, String object, Config config) {
    	if (logging)
            logger.info("performing LIST at /{}/{}", container, object);
        return new ByteArrayInputStream(new byte[] {});
    }

    @Override
    public void createContainer(String container, Config config) {
        if (logging)
            logger.info("performing PUT at /{}", container);
    }

    @Deprecated
    public void createObject(String container, String object, byte[] data,
            Config config) {
        if (logging)
            logger.info("performing PUT at /{}/{}", container, object);
    }

    @Override
    public void createObject(String container, String object, InputStream data,
            long length, Config config) {
        if (logging)
            logger.info("performing PUT at /{}/{}", container, object);
    }

    @Override
    public void deleteContainer(String container, Config config) {
        if (logging)
            logger.info("performing DELETE at /{}", container);
    }

    @Override
    public void deleteObject(String container, String object, Config config) {
        if (logging)
            logger.info("performing DELETE at /{}/{}", container, object);
    }

    protected void createMetadata(String container, String object,
            Map<String, String> map, Config config) {
        if (logging)
            logger.info("performing POST at /{}/{}", container, object);
    }

    protected Map<String, String> getMetadata(String container, String object,
            Config config) {
        if (logging)
            logger.info("performing HEAD at /{}/{}", container, object);
        return Collections.emptyMap();
    }
    public void setAuthFlag(Boolean auth) {
    	this.authFlag = auth;
    }
    public Boolean isAuthValid() {
    	return authFlag;
    }
}
