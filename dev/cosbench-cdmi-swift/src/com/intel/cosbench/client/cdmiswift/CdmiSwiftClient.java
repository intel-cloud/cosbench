package com.intel.cosbench.client.cdmiswift;

import static com.intel.cosbench.client.cdmiswift.CdmiSwiftConstants.*;
import static org.apache.http.HttpStatus.*;

import java.io.*;

import org.apache.http.client.HttpClient;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.http.*;
import org.apache.http.client.methods.*;
import org.apache.http.util.*;

import com.intel.cosbench.client.cdmi.util.CdmiJsonInputStreamEntity;

/**
 * This class encapsulates operations to access swift through cdmi middleware (https://github.com/osaddon/cdmi).
 * 
 * @author ywang19
 *
 */
public class CdmiSwiftClient {
    private static boolean REPORT_DELETE_ERROR = false;
    private HttpClient client;

    private String authToken;
    private String storageUrl;
    
    private final static String cdmi_ver = "1.0.1";

    public CdmiSwiftClient(HttpClient client) {
        this.client = client;
    }

    public void init(String authToken, String storageURL) {
        this.authToken = authToken;
        this.storageUrl = storageURL;
    }

    public void dispose() {
        client.getConnectionManager().shutdown();
    }

    public void createContainer(String container) throws IOException,
            SwiftException {
        HttpResponse response = null;
        try {
            // Create the request
            HttpPut method = new HttpPut(storageUrl + "/" + encodeURL(container));
            
            method.setHeader("Accept", "application/cdmi-container");
            method.setHeader("Content-Type", "application/cdmi-container");
            method.setHeader("X-CDMI-Specification-Version", cdmi_ver);
            method.setHeader(X_AUTH_TOKEN, authToken);
            
            response = client.execute(method);
            int statusCode = response.getStatusLine().getStatusCode();
 
			if (statusCode == SC_CREATED || statusCode == SC_ACCEPTED) {
			    return;
			}
			throw new SwiftException("unexpected return from server",
			    response.getAllHeaders(), response.getStatusLine());
        }finally {
        	if (response != null)
        		EntityUtils.consume(response.getEntity());
        }
    }

    public void deleteContainer(String container) throws IOException,
    SwiftException {
    	// add storage access logic here.    	
        HttpResponse response = null;
    	try {
            // Create the request
            HttpDelete method = new HttpDelete(storageUrl + "/" + encodeURL(container)); 
            
            method.setHeader("X-CDMI-Specification-Version", cdmi_ver);
            method.setHeader(X_AUTH_TOKEN, authToken);
            
            response = client.execute(method);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == SC_NO_CONTENT)
                return;
            if (statusCode == SC_NOT_FOUND)
                throw new SwiftFileNotFoundException("container not found: "
                        + container, response.getAllHeaders(),
                        response.getStatusLine());
            if (statusCode == SC_CONFLICT)
                throw new SwiftConflictException(
                        "cannot delete an non-empty container",
                        response.getAllHeaders(), response.getStatusLine());
            throw new SwiftException("unexpected return from server",
                    response.getAllHeaders(), response.getStatusLine());
        } finally {
            if (response != null)
                EntityUtils.consume(response.getEntity());
        }
    }

    public InputStream getObjectAsStream(String container, String object)
            throws IOException, SwiftException {
    	
    	HttpResponse response = null;
        // Create the request
        HttpGet method = new HttpGet(storageUrl + "/" + encodeURL(container)
                + "/" + encodeURL(object)); 
        
        method.setHeader("Accept", "application/cdmi-object");
        method.setHeader("X-CDMI-Specification-Version", cdmi_ver);
        method.setHeader(X_AUTH_TOKEN, authToken);

        response = client.execute(method);        
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == SC_OK)
            return response.getEntity().getContent();
        
        if (statusCode == SC_NOT_FOUND)
            throw new SwiftFileNotFoundException("object not found: "
                    + container + "/" + object, response.getAllHeaders(),
                    response.getStatusLine());
        throw new SwiftException("unexpected result from server",
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
            InputStream data, long length) throws IOException, CdmiSwiftClientException {
    	// add storage access logic here.
    	HttpPut method = null;
        // Create the request
        HttpResponse response = null;
        try {
            method = new HttpPut(storageUrl + "/" + encodeURL(container)
                    + "/" + encodeURL(object)); 
            
            method.setHeader("Accept", "application/cdmi-object");
            method.setHeader("Content-Type", "application/cdmi-object");
            method.setHeader("X-CDMI-Specification-Version", cdmi_ver);
            method.setHeader(X_AUTH_TOKEN, authToken);

            CdmiJsonInputStreamEntity entity = new CdmiJsonInputStreamEntity(data, length);
            
            if (length < 0)
                entity.setChunked(true);
            else {
                entity.setChunked(false);
            }
            
            method.setEntity(entity);

            response = client.execute(method);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_CREATED) {
                return;
            } if (statusCode == SC_ACCEPTED)
                return;
            if (statusCode == SC_NOT_FOUND)
                throw new FileNotFoundException("container not found: "
                        + container);
            else {
                throw new CdmiSwiftClientException(statusCode, "Unexpected Server Response: "
                        + response.getStatusLine(), response.getAllHeaders(),
                        response.getStatusLine());
            }
        }finally {
            if (response != null)
                EntityUtils.consume(response.getEntity());
        }
    }
  
    public void deleteObject(String container, String object)
            throws IOException, SwiftException {
    	HttpResponse response = null;
        try {
            // Create the request      
            HttpDelete method = new HttpDelete(storageUrl + "/" + encodeURL(container)
                    + "/" + encodeURL(object)); 
            
            method.setHeader("X-CDMI-Specification-Version", cdmi_ver);
            method.setHeader(X_AUTH_TOKEN, authToken);
            
            response = client.execute(method);
            
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == SC_NO_CONTENT)
                return;
            if (!REPORT_DELETE_ERROR)
                return;
            if (statusCode == SC_NOT_FOUND)
                throw new SwiftFileNotFoundException("object not found: "
                        + container + "/" + object,
                        response.getAllHeaders(), response.getStatusLine());
            throw new SwiftException("unexpected return from server",
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
