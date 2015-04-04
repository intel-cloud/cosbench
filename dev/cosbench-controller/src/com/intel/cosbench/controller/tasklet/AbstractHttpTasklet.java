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

package com.intel.cosbench.controller.tasklet;

import java.io.*;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.apache.commons.lang.StringUtils;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import com.intel.cosbench.client.http.HttpClientUtil;
import com.intel.cosbench.controller.model.TaskContext;
import com.intel.cosbench.service.*;

abstract class AbstractHttpTasklet extends AbstractTasklet {

    private static final int TIMEOUT = 300 * 1000;

    public AbstractHttpTasklet(TaskContext context) {
        super(context);
    }

    protected void initHttpClient() {
        HttpClient client = HttpClientUtil.createHttpClient(TIMEOUT);
        context.setHttpClient(client);
    }

    protected synchronized void closeHttpClient() {
        HttpClient client = context.getHttpClient();
        HttpClientUtil.disposeHttpClient(client);
//        context.setHttpClient(null);
    }

    protected String issueHttpRequest(String command, String content) {
        String url = getDriver().getUrl() + "/i/" + command + ".command";
        HttpClient client = context.getHttpClient();
        HttpPost request = prepareRequest(content, url);
        String body = null;
        HttpResponse response = null;
        try {
            response = client.execute(request);
        } catch (SocketException se) {
        	LOGGER.error("fail to POST driver with socket connection error, will give up the task" , se);
        	throw new CancelledException();
        } catch (SocketTimeoutException ste) {
            LOGGER.error("fail to POST driver with url=" + url , ste);
            throw new TaskletException(); // mark termination
        } catch (ConnectTimeoutException cte) {
            LOGGER.error("fail to POST driver with url=" + url, cte);
            throw new TaskletException(); // mark termination
        } catch (InterruptedIOException ie) {
            throw new CancelledException(); // task cancelled
        } catch (Exception e) {
            LOGGER.error("fail to POST driver with url=" + url, e);
            throw new TaskletException(); // mark termination
        } finally {
        	if(response != null) {      
//        		LOGGER.info("try to fetch response body");
        		try{
        			body = fetchResponseBody(response);
        		}catch(IOException ioe) {
        			LOGGER.error("fail to fetch response body", ioe);
        		}
        	}
        }
        return body; // HTTP response body retrieved
    }

    private static HttpPost prepareRequest(String content, String url) {
        HttpPost POST = new HttpPost(url);
        try {
            if (StringUtils.isNotEmpty(content))
                POST.setEntity(new StringEntity(content));
        } catch (Exception e) {
            throw new UnexpectedException(e); // will not happen
        }
        if (content != null && content.length() > 0)
            if (!content.startsWith("<?xml"))
                LOGGER.debug("[ >> ] - {} -> {}", content, url);
            else
                LOGGER.debug("[ >> ] - [xml-content] -> {}", url);
        else
            LOGGER.debug("[ >> ] - [empty-body] -> {}", url);
        return POST; // HTTP request prepared
    }

    private static String fetchResponseBody(HttpResponse response)
            throws IOException {
        String body = null;
        HttpEntity entity = response.getEntity();
        StatusLine status = response.getStatusLine();
        try {
            body = EntityUtils.toString(entity);
        } finally {
            EntityUtils.consume(entity);
        }
        if (body.length() < 2048)
            LOGGER.debug("[ << ] - {} {}", status, body);
        else
            LOGGER.debug("[ << ] - {} [body-omitted]", status);
        return body; // the response body
    }

}
