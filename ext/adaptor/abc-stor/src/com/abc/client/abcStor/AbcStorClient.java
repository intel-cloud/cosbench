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

package com.abc.client.abcStor;

import java.io.*;

import org.apache.http.client.HttpClient;
//import org.apache.commons.codec.EncoderException;
//import org.apache.commons.codec.net.URLCodec;
//import org.apache.http.*;
//import org.apache.http.client.methods.*;
//import org.apache.http.entity.*;
//import org.apache.http.util.*;
import org.apache.http.client.methods.HttpUriRequest;

public class AbcStorClient {

    private String authToken;

    private HttpClient client;
    /* current operation */
    private volatile HttpUriRequest request;
    
    public AbcStorClient(HttpClient client) {
        this.client = client;
    }
    
    public String getAuthToken() {
        return authToken;
    }

    public void dispose() {
    	request = null;
        client.getConnectionManager().shutdown();
    }

    public void init(String authToken) {
        this.authToken = authToken;
    }

    public void abort() {
        if (request != null)
            request.abort();
        request = null;
    }
    
    public void createContainer(String container) throws IOException,
            AbcStorClientException {
    	// add storage access logic here.
    }

    public void deleteContainer(String container) throws IOException,
    AbcStorClientException {
    	// add storage access logic here.
    }

    public InputStream getObjectAsStream(String container, String object)
            throws IOException, AbcStorClientException {
    	// add storage access logic here.
    	byte[] buf = new byte[1024];
    	return new ByteArrayInputStream(buf);
    	
    }

    public void storeStreamedObject(String container, String object,
            InputStream data, long length) throws IOException, AbcStorClientException {
    	// add storage access logic here.
    }

    public void deleteObject(String container, String object)
            throws IOException, AbcStorClientException {
    	// add storage access logic here.
    }

}
