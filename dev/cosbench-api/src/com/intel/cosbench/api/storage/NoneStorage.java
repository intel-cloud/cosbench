/**

Copyright 2013 Intel Corporation, All Rights Reserved.
Copyright 2021 EHualu Corporation, All Rights Reserved.

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
			logger.info("performing GET(get-object) at /{}/{}", container, object);
		return new ByteArrayInputStream(new byte[] {});
	}
	
	// 2021.07.13, Add restoreObject method.
	@Override
	public void restoreObject(String container, String object, Config config) {
		if (logging)
			logger.info("performing RESTORE(restore-object) at /{}/{}", container, object);
	}

	@Override
	public InputStream getList(String container, String object, Config config) {
		if (logging)
			logger.info("performing LIST(list-objects) at /{}/{}", container, object);
		return new ByteArrayInputStream(new byte[] {});
	}

	@Override
	public void createContainer(String container, Config config) {
		if (logging)
			logger.info("performing PUT(create-bucket) at /{}", container);
	}

	@Deprecated
	public void createObject(String container, String object, byte[] data, Config config) {
		if (logging)
			logger.info("performing PUT at /{}/{}", container, object);
	}

	@Override
	public void createObject(String container, String object, InputStream data, long length, Config config) {
		if (logging)
			logger.info("performing PUT(put-object) at /{}/{}", container, object);
	}
	
	// 2021.7.27, sine.
	@Override
	public void createMultipartObject(String container, String object, InputStream data, long length, Config config) {
		if (logging)
			logger.info("performing PUT(multipart-upload) at /{}/{}", container, object);
	}
	
	@Override
	public void deleteContainer(String container, Config config) {
		if (logging)
			logger.info("performing DELETE(delete-bucket) at /{}", container);
	}

	@Override
	public void deleteObject(String container, String object, Config config) {
		if (logging)
			logger.info("performing DELETE(delete-object) at /{}/{}", container, object);
	}
	
	@Override
	public void headObject(String container, String object, Config config) {
		if (logging)
			logger.info("performing HEAD(head-object) at /{}/{}", container, object);
	}

	protected void createMetadata(String container, String object, Map<String, String> map, Config config) {
		if (logging)
			logger.info("performing POST at /{}/{}", container, object);
	}

	protected Map<String, String> getMetadata(String container, String object, Config config) {
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
