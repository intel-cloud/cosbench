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

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.*;
import org.apache.http.util.EntityUtils;

import com.intel.cosbench.api.context.AuthContext;


class HttpAuthTest {
	private AbstractHttpClient client = null;
	
    /* account info */
    private String storage_url;

    public boolean getObject(AuthContext context, String conName, String objName) {
//    	host = context.getStr(AUTH_HOST_KEY);
//    	port = context.getInt(AUTH_PORT_KEY);
    	storage_url = context.getStr(STORAGE_URL_KEY);
    	client = (AbstractHttpClient)context.get(AUTH_CLIENT_KEY);
    	
        
        String url = storage_url + "/" + conName + "/" + objName;

		HttpGet method = new HttpGet(url);

		HttpResponse response = null;
    	
    	try {
    		response = client.execute(method);
    		
    		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
    			return true;
    		}
    		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED) {
    			System.out.println("unauthorized request!");
    			return false;
    		}
    	} catch(Exception e) {
    		e.printStackTrace();
    	} finally {
    		if(response != null)
    			dumpResponse(response);

    		if (method != null)
    			method.abort();
    	}
        
        return false;
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
    
    public static void main(String[] args) {
    	HttpAuthTest test = new HttpAuthTest();
    	
    	HttpAuth auth = new HttpAuth("http://localhost:8080", "cdmi", "mypass", 3000);
    	
    	test.getObject(auth.login(), "cdmi-test", "abc.txt");
    }

}
