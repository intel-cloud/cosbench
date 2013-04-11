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

import java.io.*;

import org.apache.http.*;
import org.apache.http.util.EntityUtils;


/**
 * This class encapsulates one wrapper of http response.
 * 
 * @author ywang19, qzheng7
 *
 */
public class SwiftResponse {

    private HttpResponse response = null;
    private HttpEntity entity = null;

    public SwiftResponse(HttpResponse response) {
        this.response = response;
        this.entity = response.getEntity();
    }

    public StatusLine getStatusLine() {
        return response.getStatusLine();
    }

    public int getStatusCode() {
        return response.getStatusLine().getStatusCode();
    }

    public String getStatusMessage() {
        return response.getStatusLine().getReasonPhrase();
    }

    public Header getResponseHeader(String headerName) {
        return response.getFirstHeader(headerName);
    }

    public Header[] getResponseHeaders() {
        return response.getAllHeaders();
    }

    public void consumeResposeBody() throws IOException {
        EntityUtils.consume(entity);
    }

    public InputStream getResponseBodyAsStream() throws IOException {
        return entity.getContent();
    }

    public boolean loginSuccess() {
        int statusCode = getStatusCode();
        return (statusCode >= 200 && statusCode < 300) ? true : false;
    }

    public String getAuthToken() {
        Header header = getResponseHeader(X_AUTH_TOKEN);
        return header != null ? header.getValue() : null;
    }

    public String getStorageURL() {
        Header header = getResponseHeader(X_STORAGE_URL);
        return header != null ? header.getValue() : null;
    }

    public long getContainerBytesUsed() {
        Header header = getResponseHeader(X_CONTAINER_BYTES_USED);
        return header != null ? Long.parseLong(header.getValue()) : -1L;
    }

    public int getContainerObjectCount() {
        Header header = getResponseHeader(X_CONTAINER_OBJECT_COUNT);
        return header != null ? Integer.parseInt(header.getValue()) : -1;
    }

    public long getAccountBytesUsed() {
        Header header = getResponseHeader(X_ACCOUNT_BYTES_USED);
        return header != null ? Long.parseLong(header.getValue()) : -1L;
    }

    public int getAccountContainerCount() {
        Header header = getResponseHeader(X_ACCOUNT_CONTAINER_COUNT);
        return header != null ? Integer.parseInt(header.getValue()) : -1;
    }

}
