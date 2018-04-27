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

package com.intel.cosbench.api.swift;

import static com.intel.cosbench.client.swift.SwiftConstants.*;

import java.io.*;
import java.net.SocketTimeoutException;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ConnectTimeoutException;

import com.intel.cosbench.api.context.AuthContext;
import com.intel.cosbench.api.context.DefaultAuthContext;
import com.intel.cosbench.api.storage.*;
import com.intel.cosbench.client.http.HttpClientUtil;
import com.intel.cosbench.client.swift.*;
import com.intel.cosbench.config.Config;
import com.intel.cosbench.log.Logger;

/**
 * This class encapsulates a Swift implementation for Storage API.
 * 
 * @author ywang19, qzheng7
 * 
 */
class SwiftStorage extends NoneStorage {

    private SwiftClient client;

    /* configurations */
    private int timeout; // connection and socket timeout
    private String token;
    private String storage_url;
    private String policy;
    private int transfer_rate;

    public SwiftStorage() {
        /* empty */
    }

    @Override
    public void init(Config config, Logger logger) {
        super.init(config, logger);

        timeout = config.getInt(CONN_TIMEOUT_KEY, CONN_TIMEOUT_DEFAULT);
        token = config.get(AUTH_TOKEN_KEY, AUTH_TOKEN_DEFAULT);
        storage_url = config.get(STORAGE_URL_KEY, STORAGE_URL_DEFAULT);
        policy = config.get(POLICY_KEY, POLICY_DEFAULT);
        transfer_rate = config.getInt(TRANSFER_RATE, TRANSFER_RATE_DEFAULT);
        		
        parms.put(CONN_TIMEOUT_KEY, timeout);
        parms.put(AUTH_TOKEN_KEY, token);
        parms.put(STORAGE_URL_KEY, storage_url);
        parms.put(POLICY_KEY, policy);
        parms.put(TRANSFER_RATE, transfer_rate);

        logger.debug("using storage config: {}", parms);

        HttpClient httpClient = HttpClientUtil.createHttpClient(timeout);
        
        client = new SwiftClient(httpClient);
        logger.debug("swift client has been initialized");
    }

    @Override
    public void setAuthContext(AuthContext info) {
        super.setAuthContext(info);
        
        if(info != null) {
        	token = info.getStr(AUTH_TOKEN_KEY);
        	storage_url = info.getStr(STORAGE_URL_KEY);
        }
        
        try {
            client.init(token, storage_url, policy, transfer_rate);
        } catch (Exception e) {
            throw new StorageException(e);
        }
        logger.debug(new StringBuffer()
        		.append("using auth token: ")
        		.append(token)
        		.append(", storage url: ")
        		.append(storage_url)
        		.append(", storage policy: ")
        		.append(policy).toString());
    }

    @Override
    public AuthContext getAuthContext() {
		String token = client.getAuthToken();
		String storage_url = client.getStorageURL();
	
		AuthContext info = new DefaultAuthContext();
		info.put(AUTH_TOKEN_KEY, token);
		info.put(STORAGE_URL_KEY, storage_url);
	
		logger.debug("returned auth token: {}, storage url: {}", token, storage_url);
		return info;
    }

    @Override
    public void dispose() {
        super.dispose();
        client.dispose();
    }

    @Override
    public void abort() {
        super.abort();
        client.abort();
    }

    @Override
    public InputStream getObject(String container, String object, Config config) {
        super.getObject(container, object, config);
        InputStream stream;
        try {
            stream = client.getObjectAsStream(container, object);
        } catch (SocketTimeoutException ste) {
            throw new StorageTimeoutException(ste);
        } catch (ConnectTimeoutException cte) {
            throw new StorageTimeoutException(cte);
        } catch (InterruptedIOException ie) {
            throw new StorageInterruptedException(ie);
        } catch (SwiftException se) {
            String msg = se.getHttpStatusLine().toString();
            throw new StorageException(msg, se);
        } catch (Exception e) {
            throw new StorageException(e);
        }
        return stream;
    }
    
    @Override
    public InputStream getList(String container, String object, Config config) {
        super.getList(container, object, config);
        InputStream stream;
        try {
        	stream = client.getTargetList(container, object);
        } catch (SocketTimeoutException ste) {
            throw new StorageTimeoutException(ste);
        } catch (ConnectTimeoutException cte) {
            throw new StorageTimeoutException(cte);
        } catch (InterruptedIOException ie) {
            throw new StorageInterruptedException(ie);
        } catch (SwiftException se) {
            String msg = se.getHttpStatusLine().toString();
            throw new StorageException(msg, se);
        } catch (Exception e) {
            throw new StorageException(e);
        }
        return stream;
    }

    @Override
    public void createContainer(String container, Config config) {
        super.createContainer(container, config);
        try {
            if (!client.containerExists(container))
                client.createContainer(container);
        } catch (SocketTimeoutException ste) {
            throw new StorageTimeoutException(ste);
        } catch (ConnectTimeoutException cte) {
            throw new StorageTimeoutException(cte);
        } catch (InterruptedIOException ie) {
            throw new StorageInterruptedException(ie);
        } catch (SwiftException se) {
            String msg = se.getHttpStatusLine().toString();
            throw new StorageException(msg, se);
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Deprecated
    public void createObject(String container, String object, byte[] data,
            Config config) {
        super.createObject(container, object, data, config);
        try {
            client.storeObject(container, object, data);
        } catch (SocketTimeoutException ste) {
            throw new StorageTimeoutException(ste);
        } catch (ConnectTimeoutException cte) {
            throw new StorageTimeoutException(cte);
        } catch (InterruptedIOException ie) {
            throw new StorageInterruptedException(ie);
        } catch (SwiftException se) {
            String msg = se.getHttpStatusLine().toString();
            throw new StorageException(msg, se);
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public void createObject(String container, String object, InputStream data,
            long length, Config config) {
        super.createObject(container, object, data, length, config);
        try {
            client.storeStreamedObject(container, object, data, length);
        } catch (SocketTimeoutException ste) {
            throw new StorageTimeoutException(ste);
        } catch (ConnectTimeoutException cte) {
            throw new StorageTimeoutException(cte);
        } catch (InterruptedIOException ie) {
            throw new StorageInterruptedException(ie);
        } catch (SwiftException se) {
            String msg = se.getHttpStatusLine().toString();
            throw new StorageException(msg, se);
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public void deleteContainer(String container, Config config) {
        super.deleteContainer(container, config);
        try {
            if (client.containerExists(container))
                client.deleteContainer(container);
        } catch (SocketTimeoutException ste) {
            throw new StorageTimeoutException(ste);
        } catch (ConnectTimeoutException cte) {
            throw new StorageTimeoutException(cte);
        } catch (InterruptedIOException ie) {
            throw new StorageInterruptedException(ie);
        } catch (SwiftException se) {
            String msg = se.getHttpStatusLine().toString();
            throw new StorageException(msg, se);
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public void deleteObject(String container, String object, Config config) {
        super.deleteObject(container, object, config);
        try {
            client.deleteObject(container, object);
        } catch (SocketTimeoutException ste) {
            throw new StorageTimeoutException(ste);
        } catch (ConnectTimeoutException cte) {
            throw new StorageTimeoutException(cte);
        } catch (InterruptedIOException ie) {
            throw new StorageInterruptedException(ie);
        } catch (SwiftException se) {
            String msg = se.getHttpStatusLine().toString();
            throw new StorageException(msg, se);
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    protected void createMetadata(String container, String object,
            Map<String, String> map, Config config) {
        super.createMetadata(container, object, map, config);
        try {
            client.storeObjectMetadata(container, object, map);
        } catch (SocketTimeoutException ste) {
            throw new StorageTimeoutException(ste);
        } catch (ConnectTimeoutException cte) {
            throw new StorageTimeoutException(cte);
        } catch (InterruptedIOException ie) {
            throw new StorageInterruptedException(ie);
        } catch (SwiftException se) {
            String msg = se.getHttpStatusLine().toString();
            throw new StorageException(msg, se);
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    protected Map<String, String> getMetadata(String container, String object,
            Config config) {
        super.getMetadata(container, object, config);
        try {
            return client.getObjectMetadata(container, object);
        } catch (SocketTimeoutException ste) {
            throw new StorageTimeoutException(ste);
        } catch (ConnectTimeoutException cte) {
            throw new StorageTimeoutException(cte);
        } catch (InterruptedIOException ie) {
            throw new StorageInterruptedException(ie);
        } catch (SwiftException se) {
            String msg = se.getHttpStatusLine().toString();
            throw new StorageException(msg, se);
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

}
