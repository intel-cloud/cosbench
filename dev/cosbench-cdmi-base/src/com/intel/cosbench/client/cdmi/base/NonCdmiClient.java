package com.intel.cosbench.client.cdmi.base;

import static org.apache.http.HttpStatus.*;

import java.io.*;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.http.*;
import org.apache.http.client.methods.*;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.util.*;


/**
 * This class encapsulates operations to access cdmi compatible server with non-cdmi content type.
 * 
 * @author ywang19
 *
 */
public class NonCdmiClient extends BaseCdmiClient{
//    private HttpClient client;
//    private String uri;
//    private ArrayList<Header> custom_headers = new ArrayList<Header> ();
    
    public NonCdmiClient() {
    	super();
    }

//    public void init(HttpClient httpClient, String uri, Map<String, String> headerKV) {
//    	this.client = httpClient;
//        this.uri = uri;
//        
//        for(String key: headerKV.keySet())
//        	this.custom_headers.add(new BasicHeader(key, headerKV.get(key)));    
//    }

    public void dispose() {
        client.getConnectionManager().shutdown();
    }
    
    private void setCustomHeaders(HttpRequest method) {
    	for(Header header : custom_headers)
    		method.setHeader(header);
    }

    public void createContainer(String container) throws IOException,
            CdmiException {
        HttpResponse response = null;
        try {
            // Create the request
            HttpPut method = new HttpPut(uri + "/" + encodeURL(container) + "/");
            
            setCustomHeaders(method);
            
            response = client.execute(method, httpContext);
            int statusCode = response.getStatusLine().getStatusCode();
 
			if (statusCode == SC_CREATED || statusCode == SC_ACCEPTED) {
			    return;
			}
			throw new CdmiException("unexpected return from server",
			    response.getAllHeaders(), response.getStatusLine());
        }finally {
        	if (response != null)
        		EntityUtils.consume(response.getEntity());
        }
    }

    public void deleteContainer(String container) throws IOException,
    CdmiException {
    	// add storage access logic here.    	
        HttpResponse response = null;
    	try {
            // Create the request
            HttpDelete method = new HttpDelete(uri + "/" + encodeURL(container) + "/"); // "http://localhost:8080/cdmi-server/TestContainer/");

            setCustomHeaders(method);
            
            response = client.execute(method, httpContext);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == SC_NO_CONTENT)
                return;
            if (statusCode == SC_NOT_FOUND)
                throw new CdmiFileNotFoundException("container not found: "
                        + container, response.getAllHeaders(),
                        response.getStatusLine());
            if (statusCode == SC_CONFLICT)
                throw new CdmiConflictException(
                        "cannot delete an non-empty container",
                        response.getAllHeaders(), response.getStatusLine());
            throw new CdmiException("unexpected return from server",
                    response.getAllHeaders(), response.getStatusLine());
        } finally {
            if (response != null)
                EntityUtils.consume(response.getEntity());
        }
    }

    public InputStream getObjectAsStream(String container, String object)
            throws IOException, CdmiException {
    	
    	HttpResponse response = null;
        // Create the request
        HttpGet method = new HttpGet(uri + "/" + encodeURL(container)
                + "/" + encodeURL(object)); 

        setCustomHeaders(method);
        
        response = client.execute(method, httpContext);        
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == SC_OK)
            return response.getEntity().getContent();
        
        if (statusCode == SC_NOT_FOUND)
            throw new CdmiFileNotFoundException("object not found: "
                    + container + "/" + object, response.getAllHeaders(),
                    response.getStatusLine());
        throw new CdmiException("unexpected result from server",
                response.getAllHeaders(), response.getStatusLine());
    }
    
    @SuppressWarnings("unused")
    private void dumpMethod(HttpRequestBase method) {
    	System.out.println("==== METHOD BEGIN ====");
    	System.out.println(method.getMethod());
        System.out.println(method.getURI());
        for(Header header: method.getAllHeaders()) {
        	System.out.println(header.getName() + ": " + header.getValue());
        }
        System.out.println("==== METHOD END ====");
    }
    
    @SuppressWarnings("unused")
    private void dumpResponse(HttpResponse response) {
    	System.out.println("==== RESPONSE BEGIN ====");
        Header[] hdr = response.getAllHeaders();
        System.out.println("Headers : " + hdr.length);
        for (int i = 0; i < hdr.length; i++) {
            System.out.println(hdr[i]);
        }
        System.out.println("---------");
        System.out.println(response.getProtocolVersion());
        System.out.println(response.getStatusLine().getStatusCode());

        System.out.println(response.getStatusLine().getReasonPhrase());
        System.out.println(response.getStatusLine().toString());
        System.out.println("---------");
        System.out.println("==== RESPONSE END ====");
    }
    
    public void storeStreamedObject(String container, String object,
            InputStream data, long length) throws IOException, CdmiClientException {
    	// add storage access logic here.
    	HttpPut method = null;
        // Create the request
        HttpResponse response = null;
        try {
            method = new HttpPut(uri + "/" + encodeURL(container)
                    + "/" + encodeURL(object)); 
            
            method.setHeader("Content-Type", "application/octet-stream");
            setCustomHeaders(method);
            InputStreamEntity entity = new InputStreamEntity(data, length);
            if (length < 0)
                entity.setChunked(true);
            else {
                entity.setChunked(false);
            }
            
            method.setEntity(entity);
            
            response = client.execute(method, httpContext);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_CREATED) {
                return;
            } if (statusCode == SC_ACCEPTED)
                return;
            if (statusCode == SC_NOT_FOUND)
                throw new FileNotFoundException("container not found: "
                        + container);
            else {
                throw new CdmiClientException(statusCode, "Unexpected Server Response: "
                        + response.getStatusLine(), response.getAllHeaders(),
                        response.getStatusLine());
            }
        }finally {
            if (response != null)
                EntityUtils.consume(response.getEntity());
        }
    }
  
    public void deleteObject(String container, String object)
            throws IOException, CdmiException {
    	HttpResponse response = null;
        try {
            // Create the request      
            HttpDelete method = new HttpDelete(uri + "/" + encodeURL(container)
                    + "/" + encodeURL(object)); 
            
            setCustomHeaders(method);
            
            response = client.execute(method, httpContext);
            
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == SC_NO_CONTENT)
                return;
            if (!raise_delete_errors)
                return;
            if (statusCode == SC_NOT_FOUND)
                throw new CdmiFileNotFoundException("object not found: "
                        + container + "/" + object,
                        response.getAllHeaders(), response.getStatusLine());
            throw new CdmiException("unexpected return from server",
                    response.getAllHeaders(), response.getStatusLine());
        } finally {
            if (response != null)
                EntityUtils.consume(response.getEntity());
        }
    }
    
    public static String encodeURL(String str) {
        URLCodec codec = new URLCodec();
        try {
            return codec.encode(str).replaceAll("\\+", "%20");
        } catch (EncoderException ee) {
            return str;
        }
    }    
}
