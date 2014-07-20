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

package com.intel.cosbench.client.amplistor;

import static org.apache.http.HttpStatus.SC_ACCEPTED;

import java.io.*;
import java.util.*;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.entity.*;
import org.apache.http.util.EntityUtils;

import com.intel.cosbench.client.http.HttpClientUtil;

/**
 * This class encapsulates AmpliStor related REST operations, so far no
 * authentication supported.
 * 
 * @author ywang19, qzheng7
 * 
 */
public class AmpliClient {

    private HttpClient client = null;
    /* current operation */
    private volatile HttpUriRequest request;
    private int port;
    private String host;
    private String nsRoot;

    public AmpliClient(HttpClient client, String host, int port, String nsRoot) {
        this.client = client;
        this.host = host;
        this.port = port;
        this.nsRoot = nsRoot;
    }

    public void dispose() {
    	request = null;
        HttpClientUtil.disposeHttpClient(client);
    }

    public void abort() {
        if (request != null)
            request.abort();
        request = null;
    }
    
    public boolean login() throws IOException, HttpException {
        String storageUrl = "http://" + this.host + ":" + this.port;

        HttpHead method = HttpClientUtil.makeHttpHead(storageUrl);
        HttpResponse response = null;
        try {
            response = client.execute(method);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return true;
            }

        } finally {
            if (response != null)
                EntityUtils.consume(response.getEntity());
        }

        return false;
    }

    public String StoreObject(String sourceFilename, String ampliNamespace,
            String ampliFilename) throws IOException, HttpException,
            AmpliException {
        File file = new File(sourceFilename);

        HttpPut method = null;
        HttpResponse response = null;
        try {
            String storageUrl = "http://" + this.host + ":" + this.port
                    + nsRoot;
            method = HttpClientUtil.makeHttpPut(storageUrl + "/" + HttpClientUtil.encodeURL(ampliNamespace)
                    + "/" + HttpClientUtil.encodeURL(ampliFilename));

            method.setEntity(new FileEntity(file, "application/octet-stream"));

            response = client.execute(method);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return response.getFirstHeader("ETag").getValue();
            }

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) {
                return response.getFirstHeader("ETag").getValue();
            } else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_PRECONDITION_FAILED) {
                throw new AmpliException("Etag missmatch",
                        response.getAllHeaders(), response.getStatusLine());
            } else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_LENGTH_REQUIRED) {
                throw new AmpliException("Length miss-match",
                        response.getAllHeaders(), response.getStatusLine());
            } else {
                throw new AmpliException("Unexpected Server Response: "
                        + response.getStatusLine(), response.getAllHeaders(),
                        response.getStatusLine());
            }
        } finally {
            if (response != null)
                EntityUtils.consume(response.getEntity());
        }
    }

    public String StoreStreamedObject(InputStream stream, long length,
            String ampliNamespace, String ampliFilename) throws IOException,
            HttpException, AmpliException {
        HttpPut method = null;
        HttpResponse response = null;
        try {
            String storageUrl = "http://" + this.host + ":" + this.port
                    + nsRoot;
            method = HttpClientUtil.makeHttpPut(storageUrl + "/" + HttpClientUtil.encodeURL(ampliNamespace)
                    + "/" + HttpClientUtil.encodeURL(ampliFilename));

            InputStreamEntity entity = new InputStreamEntity(stream, length);

            if (length < 0)
                entity.setChunked(true);
            else {
                entity.setChunked(false);
            }

            method.setEntity(entity);

            response = client.execute(method);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return response.getFirstHeader("ETag").getValue();
            }

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) {
                return response.getFirstHeader("ETag").getValue();
            } else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_PRECONDITION_FAILED) {
                throw new AmpliException("Etag missmatch",
                        response.getAllHeaders(), response.getStatusLine());
            } else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_LENGTH_REQUIRED) {
                throw new AmpliException("Length miss-match",
                        response.getAllHeaders(), response.getStatusLine());
            } else {
                throw new AmpliException(System.currentTimeMillis() + ": ["
                        + Thread.currentThread().getName() + "]: "
                        + ampliNamespace + "/" + ampliFilename + ": "
                        + response.getStatusLine(), response.getAllHeaders(),
                        response.getStatusLine());
            }
        } finally {
            if (response != null)
                EntityUtils.consume(response.getEntity());
        }
    }

    /*
     * If you try to PUT from a stream to a server that uses Digest
     * authorization, the operation will fail, because the authorization
     * handling will cause
     * "org.apache.http.client.NonRepeatableRequestException: Cannot retry request with a non-repeatable request entity."
     * Semi-precise explanation of this issue: Issuing a PUT from a stream leads
     * to the httpclient library using a non-repeatable (streamed) entity. But
     * the authorization process makes the library to repeat the request (1. try
     * unauthorized, 2. server say 401 Unauthorized, use Digest, 3. client
     * retries with Digest, but this will fail, due to the nature of
     * non-repeatable streamed entity).
     * 
     * the workaround is to convert streamed (non-repeatable) entity to
     * self-contained (repeatable).
     */
    public String StoreObject(byte[] data, String ampliNamespace,
            String ampliFilename) throws IOException, HttpException,
            AmpliException {
        // int len = data.length;
        HttpPut method = null;
        String storageUrl = "http://" + this.host + ":" + this.port + nsRoot;
        method = HttpClientUtil.makeHttpPut(storageUrl + "/" + HttpClientUtil.encodeURL(ampliNamespace) + "/"
                + HttpClientUtil.encodeURL(ampliFilename));

        method.setHeader("Content-Type", "application/octet-stream");
        
        HttpResponse response = null;
        try {
            method.setEntity(new ByteArrayEntity(data));
            response = client.execute(method);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) {
                return response.getFirstHeader("ETag").getValue();
            } else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return response.getFirstHeader("ETag").getValue();
            } else {
                throw new AmpliException(ampliNamespace + "/" + ampliFilename
                        + ": " + response.getStatusLine(),
                        response.getAllHeaders(), response.getStatusLine());
            }
        } finally {
            if (response != null)
                EntityUtils.consume(response.getEntity());
        }

    }

    public byte[] getObject(String namespace, String objName)
            throws IOException, HttpException, AmpliException {
        String storageUrl = "http://" + this.host + ":" + this.port + nsRoot;

        HttpGet method = HttpClientUtil.makeHttpGet(storageUrl + "/" + HttpClientUtil.encodeURL(namespace)
                + "/" + HttpClientUtil.encodeURL(objName));

        HttpResponse response = null;
        try {
            response = client.execute(method);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return EntityUtils.toByteArray(response.getEntity());
            } else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                throw new AmpliException("Namespace: " + namespace
                        + " did not have object " + objName,
                        response.getAllHeaders(), response.getStatusLine());
            }

        } finally {
            if (response != null)
                EntityUtils.consume(response.getEntity());
        }

        return null;
    }

    public InputStream getObjectAsStream(String namespace, String objName)
            throws IOException, HttpException, AmpliException {
        String storageUrl = "http://" + this.host + ":" + this.port + nsRoot;

        HttpGet method = HttpClientUtil.makeHttpGet(storageUrl + "/" + HttpClientUtil.encodeURL(namespace)
                + "/" + HttpClientUtil.encodeURL(objName));

        HttpResponse response = null;
        
    	response = client.execute(method);

        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            return response.getEntity().getContent();
        } 
        EntityUtils.consume(response.getEntity());
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
            return response.getEntity().getContent();
        } else {
            throw new AmpliException("unknown error when request object "
                    + objName + "@" + namespace, response.getAllHeaders(),
                    response.getStatusLine());
        }
    }

    public boolean deleteObject(String ampliNamespace, String name)
            throws HttpException, IOException, AmpliException {

        HttpDelete method = null;
        HttpResponse response = null;
        
        try {
            String storageUrl = "http://" + this.host + ":" + this.port
                    + nsRoot;
            method = HttpClientUtil.makeHttpDelete(storageUrl + "/"
                    + HttpClientUtil.encodeURL(ampliNamespace) + "/" + HttpClientUtil.encodeURL(name));

            method.setHeader("Content-Type", "application/octet-stream");

            response = client.execute(method);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NO_CONTENT) {
                return true;
            } else {
                throw new AmpliNotFoundException("Namespace " + ampliNamespace
                        + "/" + name + " is not found",
                        response.getAllHeaders(), response.getStatusLine());
            }
        } finally {
            if (response != null)
                    EntityUtils.consume(response.getEntity());
        }

    }

    public AmpliPolicy createPolicy(AmpliPolicy policy) throws HttpException,
            IOException, AmpliException {

        HttpPut method = null;
        HttpResponse response = null;
        try {
            String storageUrl = "http://" + this.host + ":" + this.port
                    + "/policy";
            method = HttpClientUtil.makeHttpPut(storageUrl);

            method.setHeader("Content-Type", "text/plain");
            // method.setHeader("Content-Length", "0");

            AmpliUtils.policyToRequest(method, policy);

            response = client.execute(method);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return AmpliUtils.policyFromResponse(response);
            }

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) {
                return AmpliUtils.policyFromResponse(response);
            } else {
                throw new AmpliException("Unexpected Server Response: "
                        + response.getStatusLine(), response.getAllHeaders(),
                        response.getStatusLine());
            }
        } finally {
            if (response != null)
                EntityUtils.consume(response.getEntity());
        }
    }

    public AmpliPolicy getPolicy(String policyId) throws HttpException,
            IOException, AmpliException {

        HttpGet method = null;
        HttpResponse response = null;
        try {
            String storageUrl = "http://" + this.host + ":" + this.port
                    + "/policy";
            method = HttpClientUtil.makeHttpGet(storageUrl + "/" + HttpClientUtil.encodeURL(policyId));

            method.setHeader("Content-Type", "text/plain");

            response = client.execute(method);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return AmpliUtils.policyFromResponse(response);
            } else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_BAD_REQUEST) {
                throw new AmpliNotFoundException("Policy " + policyId
                        + " is not found", response.getAllHeaders(),
                        response.getStatusLine());
            } else {
                throw new AmpliException("Unexpected Server Response: "
                        + response.getStatusLine(), response.getAllHeaders(),
                        response.getStatusLine());
            }
        } finally {
            if (response != null)
                EntityUtils.consume(response.getEntity());
        }
    }

    public AmpliNamespace createNamespace(AmpliNamespace namespace)
            throws HttpException, IOException, AmpliException {
        HttpPut method = null;
        HttpResponse response = null;
        try {
            String storageUrl = "http://" + this.host + ":" + this.port
                    + nsRoot;

            method = new HttpPut(storageUrl);

            method.setHeader("Content-Type", "text/plain");

            AmpliUtils.namespaceToRequest(method, namespace);

            response = client.execute(method);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_FORBIDDEN) {
                return namespace;
            }

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) {
                return AmpliUtils.namespaceFromResponse(response);
            } else {
                throw new AmpliException("Unexpected Server Response: "
                        + response.getStatusLine(), response.getAllHeaders(),
                        response.getStatusLine());
            }
        } finally {
            if (response != null)
                EntityUtils.consume(response.getEntity());
        }
    }

    public String createNamespace(String namespace, String policy_id)
            throws HttpException, IOException, AmpliException {
        HttpPut method = null;
        HttpResponse response = null;
        try {
            String storageUrl = "http://" + this.host + ":" + this.port
                    + nsRoot;

            method = new HttpPut(storageUrl);

            method.setHeader("Content-Type", "text/plain");

            method.setHeader(AmpliNamespace.NAME,
                    AmpliUtils.quotedString(namespace));
            method.setHeader(AmpliNamespace.POLICY_ID,
                    AmpliUtils.quotedString(policy_id));
            method.setHeader(AmpliNamespace.MASTER_NODE_ID, Long.toString(0));

            response = client.execute(method);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_FORBIDDEN) {
                return namespace;
            }

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) {
                return AmpliUtils.unquotedString(response.getFirstHeader(
                        AmpliNamespace.NAME).getValue());
            } else {
                throw new AmpliException("Unexpected Server Response: "
                        + response.getStatusLine(), response.getAllHeaders(),
                        response.getStatusLine());
            }
        } finally {
            if (response != null)
                EntityUtils.consume(response.getEntity());
        }
    }

    public AmpliNamespace getNamespace(String name) throws HttpException,
            IOException, AmpliException {

        HttpGet method = null;
        HttpResponse response = null;
        try {
            String storageUrl = "http://" + this.host + ":" + this.port
                    + nsRoot;
            method = HttpClientUtil.makeHttpGet(storageUrl + "/" + HttpClientUtil.encodeURL(name));

            method.setHeader("Content-Type", "text/plain");

            response = client.execute(method);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return AmpliUtils.namespaceFromResponse(response);
            } else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                throw new AmpliNotFoundException("Namespace " + name
                        + " is not found", response.getAllHeaders(),
                        response.getStatusLine());
            } else {
                throw new AmpliException("Unexpected Server Response: "
                        + response.getStatusLine(), response.getAllHeaders(),
                        response.getStatusLine());
            }
        } finally {
            if (response != null)
                EntityUtils.consume(response.getEntity());
        }
    }

    public boolean isNamespaceExisted(String name) throws HttpException,
            IOException, AmpliException {

        HttpHead method = null;
        HttpResponse response = null;
        try {
            String storageUrl = "http://" + this.host + ":" + this.port
                    + nsRoot;
            method = HttpClientUtil.makeHttpHead(storageUrl + "/" + HttpClientUtil.encodeURL(name));

            method.setHeader("Content-Type", "text/plain");

            response = client.execute(method);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return true;
            } else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                throw new AmpliNotFoundException("Namespace " + name
                        + " is not found", response.getAllHeaders(),
                        response.getStatusLine());
            }
        } finally {
            if (response != null)
                EntityUtils.consume(response.getEntity());
        }

        return false;
    }

    public boolean deleteNamespace(String namespace) throws HttpException,
            IOException, AmpliException {
        HttpDelete method = null;
        HttpResponse response = null;
        try {
            String storageUrl = "http://" + this.host + ":" + this.port
                    + nsRoot;
            method = HttpClientUtil.makeHttpDelete(storageUrl + "/" + HttpClientUtil.encodeURL(namespace));

            method.setHeader("Content-Type", "text/plain");

            response = client.execute(method);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NO_CONTENT) {
                return true;
            } else {
                throw new AmpliNotFoundException("Namespace " + namespace
                        + " is not found", response.getAllHeaders(),
                        response.getStatusLine());
            }
        } finally {
            if (response != null)
                EntityUtils.consume(response.getEntity());
        }
    }

    public Map<String, String> getObjectMetadata(String namespace,
            String objName) throws IOException, HttpException, AmpliException {
        String storageUrl = "http://" + this.host + ":" + this.port + nsRoot;

        HttpGet method = HttpClientUtil.makeHttpGet(storageUrl + "/" + HttpClientUtil.encodeURL(namespace)
                + "/" + HttpClientUtil.encodeURL(objName) + "?meta=http");

        HttpResponse response = null;
        try
        {
        
        	response = client.execute(method);

	        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
	            Header[] headers = response.getAllHeaders();
	            Map<String, String> map = new HashMap<String, String>();
	
	            for (Header header : headers) {
	                map.put(header.getName(), header.getValue());
	            }
	
	            return map;
	        } else {
	            throw new AmpliException(
	                    "unexpected error when request object metadata " + objName
	                            + "@" + namespace, response.getAllHeaders(),
	                    response.getStatusLine());
	        }
        } finally {
            if (response != null)
                EntityUtils.consume(response.getEntity());
        }
    }

    public void storeObjectMetadata(String namespace, String objName,
            Map<String, String> map) throws IOException, AmpliException {
        HttpResponse response = null;
        try {
            String storageUrl = "http://" + this.host + ":" + this.port
                    + nsRoot;
            HttpPost method = HttpClientUtil.makeHttpPost(storageUrl + "/"
                    + HttpClientUtil.encodeURL(namespace) + "/" + HttpClientUtil.encodeURL(objName)
                    + "?meta=http");

            for (String ele : map.keySet()) {
                method.addHeader(ele, map.get(ele));
            }

            response = client.execute(method);
            if (response.getStatusLine().getStatusCode() == SC_ACCEPTED)
                return;
            throw new AmpliException("unexpected return from server",
                    response.getAllHeaders(), response.getStatusLine());
        } finally {
            if (response != null)
                EntityUtils.consume(response.getEntity());
        }
    }

}