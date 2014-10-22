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

package com.intel.cosbench.client.swauth;

import static com.intel.cosbench.client.swauth.SwiftAuthConstants.*;

import java.io.IOException;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.util.EntityUtils;

import com.intel.cosbench.client.http.HttpClientUtil;

public class SwiftAuthClient {

    private String authURL;
    private String username;
    private String password;

    private String authToken;
    private String storageURL;

    private HttpClient client;

    public SwiftAuthClient(HttpClient client, String authUrl, String username,
            String password) {
        this.client = client;
        this.authURL = authUrl;
        this.username = username;
        this.password = password;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getStorageURL() {
        return storageURL;
    }

    public void dispose() {
        HttpClientUtil.disposeHttpClient(client);
    }

    public void login() throws IOException, SwiftAuthClientException {
        HttpResponse response = null;
        try {
            HttpGet method = new HttpGet(authURL);
            method.setHeader(X_STORAGE_USER, username);
            method.setHeader(X_STORAGE_PASS, password);
            response = client.execute(method);
            if ((response.getStatusLine().getStatusCode() >= HttpStatus.SC_OK) &&
				(response.getStatusLine().getStatusCode() < (HttpStatus.SC_OK + 100))
				) {
                authToken = response.getFirstHeader(X_AUTH_TOKEN) != null ? response
                        .getFirstHeader(X_AUTH_TOKEN).getValue() : null;
                storageURL = response.getFirstHeader(X_STORAGE_URL) != null ? response
                        .getFirstHeader(X_STORAGE_URL).getValue() : null;
                return;
            }
            throw new SwiftAuthClientException(response.getStatusLine()
                    .getStatusCode(), response.getStatusLine()
                    .getReasonPhrase());
        } finally {
            if (response != null)
                EntityUtils.consume(response.getEntity());
        }
    }
    
    public boolean check() {
    	       HttpResponse response;
    	       if(storageURL == null || authToken == null)
    	    	   return false;
    	       try {
    	           HttpHead method = new HttpHead(storageURL);
    	           method.setHeader(X_AUTH_TOKEN, authToken);
    	           response = client.execute(method);
    	
    	           if ((response.getStatusLine().getStatusCode() >= HttpStatus.SC_OK) &&
    	               (response.getStatusLine().getStatusCode() < (HttpStatus.SC_OK + 100)))
    	              return true;
    	       } catch (IOException e) {
    	       }
    	        return false;
    }
    

}
