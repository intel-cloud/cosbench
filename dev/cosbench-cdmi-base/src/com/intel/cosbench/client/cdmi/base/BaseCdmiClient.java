/**

Copyright 2013 Intel Corporation, All Rights Reserved.
Copyright 2019 OpenIO Corporation, All Rights Reserved.

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
package com.intel.cosbench.client.cdmi.base;

import java.io.*;
import java.util.*;

import org.apache.http.Header;
import org.apache.http.auth.params.AuthPNames;
import org.apache.http.client.*;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.*;


public abstract class BaseCdmiClient {
    protected boolean raise_delete_errors = false;
    protected HttpClient client;
    protected HttpContext httpContext;
    protected String uri;
    protected ArrayList<Header> custom_headers = new ArrayList<Header> ();
//
//    public BaseCdmiClient() {
//        this.raise_delete_errors = flag;
//    }

    public void init(HttpClient httpClient, String uri, Map<String, String> headerKV, boolean flag) {
        this.client = httpClient;
        this.httpContext = new BasicHttpContext();
        httpContext.setAttribute(AuthPNames.TARGET_AUTH_PREF, Arrays.asList(new String[] {AuthPolicy.BASIC, AuthPolicy.DIGEST}));

        final AuthCache authCache = new BasicAuthCache();
        httpContext.setAttribute(ClientContext.AUTH_CACHE, authCache);
        this.uri = uri;
        this.raise_delete_errors = flag;

        for(String key: headerKV.keySet())
            this.custom_headers.add(new BasicHeader(key, headerKV.get(key)));
    }

    public abstract void dispose();

    public abstract void createContainer(String container) throws IOException, CdmiException;
    public abstract void deleteContainer(String container) throws IOException, CdmiException;
    public abstract InputStream getObjectAsStream(String container, String object) throws IOException, CdmiException;

    public abstract void storeStreamedObject(String container, String object,
            InputStream data, long length) throws IOException, CdmiClientException;
    public abstract void deleteObject(String container, String object)
            throws IOException, CdmiException;
}
