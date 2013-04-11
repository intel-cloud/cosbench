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

package com.intel.cosbench.client.keystone.handler;

import java.io.*;
import java.net.SocketTimeoutException;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import com.intel.cosbench.client.http.HttpClientUtil;
import com.intel.cosbench.client.keystone.*;

public class HttpAuthHandler implements AuthHandler {

    private static final String PATH = "/tokens";

    private HttpClient client;
    private String url;
    private KeystoneMapper mapper = new KeystoneMapper();

    public HttpAuthHandler(String url, int timeout) {
        this.url = url;
        client = HttpClientUtil.createHttpClient(timeout);
    }

    public String getUrl() {
        return url;
    }

    @Override
    public KeystoneResponse POST(KeystoneRequest request) {
        HttpPost method = new HttpPost(url + PATH);
        prepareRequest(method, request);
        HttpResponse response = null;
        try {
            response = client.execute(method);
        } catch (SocketTimeoutException stex) {
            throw new KeystoneTimeoutException(stex);
        } catch (ConnectTimeoutException ctex) {
            throw new KeystoneTimeoutException(ctex);
        } catch (InterruptedIOException iex) {
            throw new KeystoneInterruptedException(iex);
        } catch (Exception ex) {
            String e = "error receiving response from the keystone";
            throw new KeystoneServerException(e, ex);
        }
        return parseResponse(response.getStatusLine(), response.getEntity());
    }

    private void prepareRequest(HttpPost method, KeystoneRequest request) {
        HttpEntity entity = null;
        try {
            String content = mapper.toJson(request);
            entity = new StringEntity(content);
        } catch (Exception ex) {
            String e = "cannot create any http entity from the request obj";
            throw new KeystoneClientException(e, ex); // should never happen
        }
        method.setEntity(entity);
        method.addHeader("Content-Type", "application/json"); // hard coded
    }

    private KeystoneResponse parseResponse(StatusLine status, HttpEntity entity) {
        String json = null;
        int code = status.getStatusCode();
        try {
            if (code < 200 || code >= 300)
                throw new KeystoneAuthException();
            json = EntityUtils.toString(entity);
        } catch (ParseException pe) {
            String e = "error parsing keystone response to string";
            throw new KeystoneServerException(e, pe);
        } catch (IOException ioe) {
            String e = "error reading response from keystone";
            throw new KeystoneServerException(e, ioe);
        } finally {
            clearResponse(entity);
        }
        return mapper.fromJson(json, KeystoneResponse.class);
    }

    private void clearResponse(HttpEntity entity) {
        try {
            EntityUtils.consume(entity);
        } catch (IOException ioe) {
            String e = "error consuming response from keystone";
            throw new KeystoneServerException(e, ioe);
        }
    }

    public void dispose() {
        HttpClientUtil.disposeHttpClient(client);
    }

}
