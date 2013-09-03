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

package com.abc.client.abcAuth;

import static com.abc.client.abcAuth.AbcAuthConstants.*;

import java.io.*;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
//import org.apache.commons.codec.EncoderException;
//import org.apache.commons.codec.net.URLCodec;
//import org.apache.http.*;
//import org.apache.http.client.methods.*;
//import org.apache.http.entity.*;
//import org.apache.http.util.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

public class AbcAuthClient {
   
    private String authURL;
    private String username;
    private String password;

    private String authToken;

    private HttpClient client;

    public AbcAuthClient(HttpClient client, String authUrl, String username,
            String password) {
        this.client = client;
        this.authURL = authUrl;
        this.username = username;
        this.password = password;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void init(String authToken) {
        this.authToken = authToken;
    }
    
    public void dispose() {
        client.getConnectionManager().shutdown();
    }

    public void login() throws IOException, AbcAuthClientException {
        HttpResponse response = null;
        try {
            HttpGet method = new HttpGet(authURL);
            method.setHeader(X_STORAGE_USER, username);
            method.setHeader(X_STORAGE_PASS, password);
            response = client.execute(method);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {  
                authToken = response.getFirstHeader(X_AUTH_TOKEN) != null ? response.getFirstHeader(X_AUTH_TOKEN).getValue() : null;
                return; 
            }
            throw new AbcAuthClientException(response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase());
        } finally {
            if (response != null)
            	EntityUtils.consume(response.getEntity());
        }
    }

}
