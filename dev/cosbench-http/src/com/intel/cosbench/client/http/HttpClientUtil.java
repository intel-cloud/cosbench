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

package com.intel.cosbench.client.http;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HttpContext;
import org.omg.CORBA.PUBLIC_MEMBER;

/**
 * This class encapsulates basic HTTP client related functions which are
 * necessary for REST based storage system.
 * 
 * @author ywang19, qzheng7
 * 
 */
public class HttpClientUtil {

    /**
     * Creates a default HTTP client with a given timeout setting.<br />
     * 
     * @param timeout
     *            the timeout in seconds that will be honored by this client
     * @return a new HTTP client
     */
    public static HttpClient createHttpClient(int timeout) {
    	// make it support self-signed certification for https.
        HttpParams params = new BasicHttpParams();
        /* default HTTP parameters */
        DefaultHttpClient.setDefaultHttpParams(params);
        /* connection/socket timeouts */
        HttpConnectionParams.setSoTimeout(params, timeout);
        HttpConnectionParams.setConnectionTimeout(params, timeout);        
        /* user agent */
        HttpProtocolParams.setUserAgent(params, "cosbench/2.0");
        
	    SchemeRegistry sr = new SchemeRegistry();          
		sr.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
		sr.register(new Scheme("https", 443, createSSLSocketFactory()));			
		ClientConnectionManager cm = new ThreadSafeClientConnManager(sr);
	
	    return new DefaultHttpClient(cm, params);    	    	
    }
    
    @SuppressWarnings({ "deprecation"})
	private static SSLSocketFactory createSSLSocketFactory()
    {
    	try
    	{
	    	SSLContext ctx = SSLContext.getInstance("TLS"); 
	        X509TrustManager tm = new X509TrustManager() { 
	        	@Override
	            public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException { 
	            } 
	        	@Override
	            public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException { 
	            } 
	
	            public X509Certificate[] getAcceptedIssuers() { 
	                return null; 
	            }
	        }; 
	        ctx.init(null, new X509TrustManager[]{tm}, null);
	        String[] enabled = {"SSL_RSA_WITH_NULL_MD5","SSL_RSA_WITH_NULL_SHA"};
	        ctx.createSSLEngine().setEnabledCipherSuites(enabled);
	        
	        SSLSocketFactory ssf = new SSLSocketFactory(ctx); 
	        ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);     
	        
            return ssf;
        } catch (Exception ex) { 
            ex.printStackTrace(); 
            return null; 
        } 	
    }

    /**
     * Releases the resources held by the given HTTP client.<br />
     * Note that no further connections can be made upon a disposed HTTP client.
     * 
     * @param client
     *            the HTTP client to be disposed.
     */
    public static void disposeHttpClient(HttpClient client) {
    	if(client != null) {
	        ClientConnectionManager manager = client.getConnectionManager();
	        manager.shutdown();
	        client = null;
    	}
    }

    public static HttpGet makeHttpGet(String url) {
    	return new HttpGet(url);
    }

    public static HttpPut makeHttpPut(String url) {
        return new HttpPut(url);
    }

    public static HttpHead makeHttpHead(String url) {
        return new HttpHead(url);
    }

    public static HttpPost makeHttpPost(String url) {
        return new HttpPost(url);
    }

    public static HttpDelete makeHttpDelete(String url) {
        return new HttpDelete(url);
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
