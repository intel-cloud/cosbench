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

package com.intel.cosbench.client.swift;

import static com.intel.cosbench.client.swift.SwiftConstants.*;
import static org.apache.http.HttpStatus.*;

import java.io.*;
import java.util.*;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.entity.*;
import com.intel.cosbench.client.http.HttpClientUtil;
import com.intel.cosbench.log.*;

public class SwiftClient {

	private static boolean REPORT_DELETE_ERROR = false;

    /* user context */
    private String authToken;
    private String storageURL;
    private String policy;
    private int rate;

    /* HTTP client */
    private HttpClient client;

    /* current operation */
    private volatile HttpUriRequest method;

    public SwiftClient(HttpClient client) {
        this.client = client;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getStorageURL() {
        return storageURL;
    }

    public void dispose() {
        method = null;
        HttpClientUtil.disposeHttpClient(client);
    }

    public void abort() {
        if (method != null)
            method.abort();
        method = null;
    }

    public void init(String authToken, String storageURL, String policy, int rate) {
        this.authToken = authToken;
        this.storageURL = storageURL;
        this.policy = policy;
        this.rate = rate;
    }

    public SwiftAccount getAccountInfo() throws IOException, SwiftException {
        SwiftResponse response = null;
        try {
            method = HttpClientUtil.makeHttpHead(storageURL);
            method.setHeader(X_AUTH_TOKEN, authToken);
            response = new SwiftResponse(client.execute(method));
            if (response.getStatusCode() == SC_NO_CONTENT) {
                long bytesUsed = response.getAccountBytesUsed();
                int containerCount = response.getAccountContainerCount();
                return new SwiftAccount(bytesUsed, containerCount);
            }
            throw new SwiftException("unexpected return from server",
                    response.getResponseHeaders(), response.getStatusLine());
        } finally {
            if (response != null)
                response.consumeResposeBody();
        }
    }

    public SwiftContainer getContainerInfo(String container)
            throws IOException, SwiftException {
        SwiftResponse response = null;
        try {
            method = HttpClientUtil.makeHttpHead(getContainerPath(container));
            method.setHeader(X_AUTH_TOKEN, authToken);
            response = new SwiftResponse(client.execute(method));
            if (response.getStatusCode() == SC_NO_CONTENT) {
                long bytesUsed = response.getContainerBytesUsed();
                int objectCount = response.getContainerObjectCount();
                return new SwiftContainer(container, bytesUsed, objectCount);
            }
            if (response.getStatusCode() == SC_NOT_FOUND)
                throw new SwiftFileNotFoundException("container not found: "
                        + container, response.getResponseHeaders(),
                        response.getStatusLine());
            throw new SwiftException("unexpected return from server",
                    response.getResponseHeaders(), response.getStatusLine());
        } finally {
            if (response != null)
                response.consumeResposeBody();
        }
    }

    public void createContainer(String container) throws IOException,
            SwiftException {
        SwiftResponse response = null;
        try {
		Logger logger = LogFactory.getSystemLogger();
		logger.debug("Creating container with auth_token " + authToken);

            method = HttpClientUtil.makeHttpPut(getContainerPath(container));
            method.setHeader(X_AUTH_TOKEN, authToken);
            if(policy != null)
            	method.setHeader(X_STORAGE_POLICY, policy);
            response = new SwiftResponse(client.execute(method));
            if (response.getStatusCode() == SC_CREATED) {
            	logger.debug("Creating container "+container+" SUCCESS");
                return;
            }
            if (response.getStatusCode() == SC_ACCEPTED) {
            	logger.debug("Creating container "+container+" SUCCESS");
                return;
            }
            throw new SwiftException("unexpected return from server",
                    response.getResponseHeaders(), response.getStatusLine());
        } finally {
            if (response != null)
                response.consumeResposeBody();
        }
    }

    public void deleteContainer(String container) throws IOException,
            SwiftException {
        SwiftResponse response = null;
        try {
            method = HttpClientUtil.makeHttpDelete(getContainerPath(container));
            method.setHeader(X_AUTH_TOKEN, authToken);
            response = new SwiftResponse(client.execute(method));
            if (response.getStatusCode() == SC_NO_CONTENT)
                return;
            if (response.getStatusCode() == SC_NOT_FOUND)
                throw new SwiftFileNotFoundException("container not found: "
                        + container, response.getResponseHeaders(),
                        response.getStatusLine());
            if (response.getStatusCode() == SC_CONFLICT)
                throw new SwiftConflictException(
                        "cannot delete an non-empty container " + container,
                        response.getResponseHeaders(), response.getStatusLine());
            throw new SwiftException("unexpected return from server",
                    response.getResponseHeaders(), response.getStatusLine());
        } finally {
            if (response != null)
                response.consumeResposeBody();
        }
    }

    public InputStream getObjectAsStream(String container, String object)
            throws IOException, SwiftException {
        method = HttpClientUtil.makeHttpGet(getObjectPath(container, object));
        method.setHeader(X_AUTH_TOKEN, authToken);
        method.setHeader(X_TRANSFER_RATE, new Integer(rate).toString());
        SwiftResponse response = new SwiftResponse(client.execute(method));
        if (response.getStatusCode() == SC_OK)
            return response.getResponseBodyAsStream();
        response.consumeResposeBody();
        if (response.getStatusCode() == SC_NOT_FOUND)
            throw new SwiftFileNotFoundException("object not found " + container + " / " + object, response.getResponseHeaders(),
                    response.getStatusLine());
        throw new SwiftException("unexpected result from server",
                response.getResponseHeaders(), response.getStatusLine());
    }
    
    public InputStream getTargetList(String container, String object) throws IOException, SwiftException {
    	if (object.isEmpty())
    		method = HttpClientUtil.makeHttpGet(getObjectPath(container, object));
		else
			method = HttpClientUtil.makeHttpHead(getObjectPath(container, object));
        method.setHeader(X_AUTH_TOKEN, authToken);
        SwiftResponse response = new SwiftResponse(client.execute(method));
        
        if (response.getStatusCode() == SC_OK) {
        	if (!object.isEmpty() && response != null)
				response.consumeResposeBody();
            return object.isEmpty() ? response.getResponseBodyAsStream()
            		: new ByteArrayInputStream(new byte[] {});
        }
        response.consumeResposeBody();
        if (response.getStatusCode() == SC_NOT_FOUND)
            throw new SwiftFileNotFoundException("list target not found " + container + " / " 
            		+ object, response.getResponseHeaders(), response.getStatusLine());
        throw new SwiftException("unexpected result from server",
                response.getResponseHeaders(), response.getStatusLine());
	}

    public void storeObject(String container, String object, byte[] data)
            throws IOException, SwiftException {
        SwiftResponse response = null;
        try {
            method = HttpClientUtil.makeHttpPut(getObjectPath(container, object));
            method.setHeader(X_AUTH_TOKEN, authToken);
            ByteArrayEntity entity = new ByteArrayEntity(data);
            entity.setChunked(false);
            entity.setContentType("application/octet-stream");
            ((HttpPut)method).setEntity(entity);
            response = new SwiftResponse(client.execute(method));
            if (response.getStatusCode() == SC_CREATED)
                return;
            if (response.getStatusCode() == SC_ACCEPTED)
                return;
            if (response.getStatusCode() == SC_NOT_FOUND)
                throw new SwiftFileNotFoundException("container not found", response.getResponseHeaders(),
                        response.getStatusLine());
            throw new SwiftException("unexpected return from server",
                    response.getResponseHeaders(), response.getStatusLine());
        } finally {
            if (response != null)
                response.consumeResposeBody();
        }
    }

    public void storeStreamedObject(String container, String object,
            InputStream data, long length) throws IOException, SwiftException {
        SwiftResponse response = null;
        try {
            method = HttpClientUtil.makeHttpPut(getObjectPath(container, object));
            method.setHeader(X_AUTH_TOKEN, authToken);
            InputStreamEntity entity = new InputStreamEntity(data, length);
            if (length < 0)
                entity.setChunked(true);
            else
                entity.setChunked(false);
            entity.setContentType("application/octet-stream");
            ((HttpPut)method).setEntity(entity);
            response = new SwiftResponse(client.execute(method));
            if (response.getStatusCode() == SC_CREATED)
                return;
            if (response.getStatusCode() == SC_ACCEPTED)
                return;
            if (response.getStatusCode() == SC_NOT_FOUND)
                throw new SwiftFileNotFoundException("container not found " + container  + "/" + object
                        , response.getResponseHeaders(),
                        response.getStatusLine());
            throw new SwiftException("unexpected return from server",
                    response.getResponseHeaders(), response.getStatusLine());
        } finally {
            if (response != null)
                response.consumeResposeBody();
        }
    }

    public void deleteObject(String container, String object)
            throws IOException, SwiftException {
        SwiftResponse response = null;
        try {
            method = HttpClientUtil.makeHttpDelete(getObjectPath(container, object));
            method.setHeader(X_AUTH_TOKEN, authToken);
            response = new SwiftResponse(client.execute(method));
            if (response.getStatusCode() == SC_NO_CONTENT)
                return;
            if (!REPORT_DELETE_ERROR)
                return;
            if (response.getStatusCode() == SC_NOT_FOUND)
                throw new SwiftFileNotFoundException("object not found " + container + "/" + object,
                        response.getResponseHeaders(), response.getStatusLine());
            throw new SwiftException("unexpected return from server",
                    response.getResponseHeaders(), response.getStatusLine());
        } finally {
            if (response != null)
                response.consumeResposeBody();
        }
    }

    public void storeObjectMetadata(String container, String object,
            Map<String, String> map) throws IOException, SwiftException {
        SwiftResponse response = null;
        try {
            method = HttpClientUtil.makeHttpPost(getObjectPath(container, object));
            method.setHeader(X_AUTH_TOKEN, authToken);
            for (String ele : map.keySet())
                method.addHeader(ele, map.get(ele));
            response = new SwiftResponse(client.execute(method));
            if (response.getStatusCode() == SC_ACCEPTED)
                return;
            if (response.getStatusCode() == SC_NOT_FOUND)
                throw new SwiftFileNotFoundException("object not found: "
                        + container + "/" + object,
                        response.getResponseHeaders(), response.getStatusLine());
            throw new SwiftException("unexpected return from server",
                    response.getResponseHeaders(), response.getStatusLine());
        } finally {
            if (response != null)
                response.consumeResposeBody();
        }
    }

    public Map<String, String> getObjectMetadata(String container, String object)
            throws IOException, SwiftException {
        SwiftResponse response = null;
        try {
            method = HttpClientUtil.makeHttpHead(getObjectPath(container, object));
            method.setHeader(X_AUTH_TOKEN, authToken);
            response = new SwiftResponse(client.execute(method));
            if (response.getStatusCode() == SC_OK) {
                Header[] headers = response.getResponseHeaders();
                Map<String, String> map = new HashMap<String, String>();
                for (Header header : headers)
                    map.put(header.getName(), header.getValue());
                return map;
            }
            if (response.getStatusCode() == SC_NOT_FOUND)
                throw new SwiftFileNotFoundException("object not found: "
                        + container + "/" + object,
                        response.getResponseHeaders(), response.getStatusLine());
            throw new SwiftException("unexpected result from server",
                    response.getResponseHeaders(), response.getStatusLine());
        } finally {
            if (response != null)
                response.consumeResposeBody();
        }
    }

    public boolean containerExists(String container) throws IOException,
            HttpException {
        try {
            getContainerInfo(container);
        } catch (SwiftException se) {
            return false;
        }
        return true;
    }

    private String getContainerPath(String container) {
        return storageURL + "/" + HttpClientUtil.encodeURL(container);
    }

    private String getObjectPath(String container, String object) {
        return getContainerPath(container) + "/" + HttpClientUtil.encodeURL(object);
    }

}
