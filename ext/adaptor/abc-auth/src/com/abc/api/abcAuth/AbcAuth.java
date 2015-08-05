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

package com.abc.api.abcAuth;

import static com.abc.client.abcAuth.AbcAuthConstants.*;

import java.io.*;
import java.net.SocketTimeoutException;

import org.apache.http.client.HttpClient;

import com.abc.client.abcAuth.*;
import com.intel.cosbench.api.auth.*;
import com.intel.cosbench.api.context.*;
import com.intel.cosbench.client.http.*;
import com.intel.cosbench.config.Config;
import com.intel.cosbench.log.Logger;

public class AbcAuth extends NoneAuth {

    private AbcAuthClient client;
    
    private String url;
    private String username;
    private String password;
    private int timeout;

    @Override
    public void init(Config config, Logger logger) {
        super.init(config, logger);
        initParms(config);
        
        HttpClient httpClient = HttpClientUtil.createHttpClient(timeout);
        client = new AbcAuthClient(httpClient, url, username, password);
    }

    private void initParms(Config config) {
        url = config.get(AUTH_URL_KEY, URL_DEFAULT);
        username = config.get(AUTH_USERNAME_KEY, AUTH_USERNAME_DEFAULT);
        password = config.get(AUTH_PASSWORD_KEY, AUTH_PASSWORD_DEFAULT);
        timeout = config.getInt(CONN_TIMEOUT_KEY, CONN_TIMEOUT_DEFAULT);
        
    	parms.put(AUTH_URL_KEY, url);    	
    	parms.put(AUTH_USERNAME_KEY, username);
    	parms.put(AUTH_PASSWORD_KEY, password);
    	parms.put(CONN_TIMEOUT_KEY, timeout);
    	
    	logger.debug("using storage config: {}", parms);
    }
    
    @Override
    public void dispose() {
        super.dispose();
        client.dispose();
    }

    @Override
    public AuthContext login() {
        super.login();
        try {
            client.login();
        } catch (SocketTimeoutException te) {
            throw new AuthTimeoutException(te);
        } catch (InterruptedIOException ie) {
            throw new AuthInterruptedException(ie);
        } catch (AbcAuthClientException se) {            
            throw new AuthException(se.getMessage(), se);
        } catch (Exception e) {
            throw new AuthException(e);
        }
        return createContext();
    }

    private AuthContext createContext() {
        AuthContext context = new DefaultAuthContext();
        context.put(AUTH_TOKEN_KEY, client.getAuthToken());
        return context;
    }
}
