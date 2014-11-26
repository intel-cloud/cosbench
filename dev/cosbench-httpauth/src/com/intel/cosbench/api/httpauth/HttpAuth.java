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

package com.intel.cosbench.api.httpauth;

import static com.intel.cosbench.client.httpauth.HttpAuthConstants.*;

import java.io.InterruptedIOException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AUTH;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.auth.params.AuthPNames;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.intel.cosbench.api.auth.AuthException;
import com.intel.cosbench.api.auth.AuthInterruptedException;
import com.intel.cosbench.api.auth.AuthTimeoutException;
import com.intel.cosbench.api.auth.NoneAuth;
import com.intel.cosbench.api.context.AuthContext;
import com.intel.cosbench.api.context.DefaultAuthContext;
import com.intel.cosbench.client.http.HttpClientUtil;
import com.intel.cosbench.config.Config;
import com.intel.cosbench.log.Logger;

/**
 * This class encapsulates a Http BASIC/DIGEST Authentication implementation for the Auth-API.
 * 
 * @author ywang19
 * 
 */
class HttpAuth extends NoneAuth {
	private AbstractHttpClient client = null;
	
    /* account info */
    private String auth_url;
    private String username;
    private String password;

    /* connection setting */
    private int timeout;

    public HttpAuth() {
        /* empty */
    }
    
    public HttpAuth(String url, String username, String password, int timeout) {
//    	this.host = host;
//    	this.port = port;
    	this.auth_url = url;
    	this.username = username;
    	this.password = password;
    	this.timeout = timeout;
    	
    	this.client = new DefaultHttpClient();
    	this.client.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, timeout);
    }

    @Override
    public void init(Config config, Logger logger) {
        super.init(config, logger);

//        protocol = config.get(AUTH_PROTOCOL_KEY, AUTH_PROTOCOL_DEFAULT);
//        host = config.get(AUTH_HOST_KEY, AUTH_HOST_DEFAULT);
//        port = config.getInt(AUTH_PORT_KEY, AUTH_PORT_DEFAULT);
        auth_url = config.get(AUTH_URL_KEY, AUTH_URL_DEFAULT);
        username = config.get(AUTH_USERNAME_KEY, AUTH_USERNAME_DEFAULT);
        password = config.get(AUTH_PASSWORD_KEY, AUTH_PASSWORD_DEFAULT);        
        timeout = config.getInt(CONN_TIMEOUT_KEY, CONN_TIMEOUT_DEFAULT);

        logger.debug("using auth config: {}", parms);

        client = (DefaultHttpClient)HttpClientUtil.createHttpClient(timeout);
        
        logger.debug("httpauth client has been initialized");
    }
    
    @Override
    public AuthContext login() {
        super.login();

//        HttpHost host = new HttpHost();
//        HttpHost targetHost = new HttpHost(host, port, protocol);

        
		URI uri;
		
		try {
			uri = new URI(auth_url);
		}catch(URISyntaxException use) {
			throw new AuthException(use);
		}
		
		HttpGet method = new HttpGet(auth_url);
		method.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, timeout);

        client.getCredentialsProvider().setCredentials(new AuthScope(uri.getHost(), uri.getPort()), 
    			new UsernamePasswordCredentials(this.username, this.password));
        
        HttpContext localContext = new BasicHttpContext();        
    	localContext.setAttribute(AuthPNames.TARGET_AUTH_PREF, Arrays.asList(new String[] {AuthPolicy.BASIC, AuthPolicy.DIGEST}));
  	
    	HttpResponse response = null;
    	
    	try {
    		dumpClientSettings();
    		response = client.execute(method, localContext);
    		
    		dumpClientSettings();
    		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
    			return createContext();
    		}
    		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED) {
    			throw new AuthException(response.getStatusLine().getReasonPhrase());
    		}
    	}catch (SocketTimeoutException ste) {
            throw new AuthTimeoutException(ste);
        } catch (ConnectTimeoutException cte) {
            throw new AuthTimeoutException(cte);
        } catch (InterruptedIOException ie) {
            throw new AuthInterruptedException(ie);
        } catch (Exception e) {
            throw new AuthException(e);
    	} finally {
    		if(response != null)
    			try {
    				dumpResponse(response);
    				EntityUtils.consume(response.getEntity());
    			}catch(Exception ignore) {
    				ignore.printStackTrace();
    			}
    			
    		if (method != null)
    			method.abort();
    	}
        
        return createContext();
    }
 
    
    private AuthContext createContext() {
//        AuthContext context = new DefaultAuthContext();
//        context.put(AUTH_CLIENT_KEY, client);
//        context.put(STORAGE_URL_KEY, auth_url);
//        
//        return context;
        HttpAuthContext context = new HttpAuthContext(auth_url, username, password, client);
        
        return context;
    }
    
    private void dumpClientSettings() {
    	System.out.println(client.getAuthSchemes().getSchemeNames());
    	System.out.println(client.getCredentialsProvider());
    	
    }
    private void dumpResponse(HttpResponse response) {
    	try {
	    	System.out.println("\nStatus Line");
	    	System.out.println("-----------");
	    	System.out.println(response.getStatusLine());
	    	
	    	Header authHeader = response.getFirstHeader(AUTH.WWW_AUTH);
	    	System.out.println("Auth Header = " + authHeader);
	    	Header authRspHeader = response.getFirstHeader(AUTH.WWW_AUTH_RESP);
	    	System.out.println("Auth Rsp Header = " + authRspHeader);
	    	System.out.println("\nHeaders");
	    	System.out.println("-------");
			for(Header header : response.getAllHeaders()) {
				System.out.println(header.toString());
			}

			System.out.println("\nBody");
			System.out.println("----");
			System.out.println(EntityUtils.toString(response.getEntity()));
			EntityUtils.consume(response.getEntity());
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    }
}
