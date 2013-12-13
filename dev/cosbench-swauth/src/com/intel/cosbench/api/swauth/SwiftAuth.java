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

package com.intel.cosbench.api.swauth;

import static com.intel.cosbench.client.swauth.SwiftAuthConstants.*;

import java.io.InterruptedIOException;
import java.net.SocketTimeoutException;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ConnectTimeoutException;

import com.intel.cosbench.api.auth.*;
import com.intel.cosbench.api.context.AuthContext;
import com.intel.cosbench.client.http.HttpClientUtil;
import com.intel.cosbench.client.swauth.*;
import com.intel.cosbench.config.Config;
import com.intel.cosbench.log.Logger;

/**
 * This class encapsulates a Swauth implementation for the Auth-API.
 * 
 * @author ywang19, qzheng7
 * 
 */
class SwiftAuth extends NoneAuth {

    private SwiftAuthClient client;

    /* account info */
    private String url;
    private String username;
    private String password;

    /* connection setting */
    private int timeout;

    public SwiftAuth() {
        /* empty */
    }

    @Override
    public void init(Config config, Logger logger) {
        super.init(config, logger);

        url = config.get(AUTH_URL_KEY,
                config.get(AUTH_URL_KEY_BACK, URL_DEFAULT));

        username = config.get(AUTH_USERNAME_KEY, AUTH_USERNAME_DEFAULT);
        password = config.get(AUTH_PASSWORD_KEY, AUTH_PASSWORD_DEFAULT);
        timeout = config.getInt(CONN_TIMEOUT_KEY, CONN_TIMEOUT_DEFAULT);

        parms.put(AUTH_URL_KEY, url);
        parms.put(AUTH_USERNAME_KEY, username);
        parms.put(AUTH_PASSWORD_KEY, password);
        parms.put(CONN_TIMEOUT_KEY, timeout);

        logger.debug("using auth config: {}", parms);

        HttpClient httpClient = HttpClientUtil.createHttpClient(timeout);
        client = new SwiftAuthClient(httpClient, url, username, password);
        logger.debug("swauth client has been initialized");
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
        } catch (SocketTimeoutException ste) {
            throw new AuthTimeoutException(ste);
        } catch (ConnectTimeoutException cte) {
            throw new AuthTimeoutException(cte);
        } catch (InterruptedIOException ie) {
            throw new AuthInterruptedException(ie);
        } catch (SwiftAuthClientException se) {
            throw new AuthException(se.getMessage(), se);
        } catch (Exception e) {
            throw new AuthException(e);
        }
        return createContext();
    }

    private AuthContext createContext() {
        AuthContext context = new AuthContext();
        context.put(AUTH_TOKEN_KEY, client.getAuthToken());
        context.put(STORAGE_URL_KEY, client.getStorageURL());
        logger.debug("login is succeed with returned values: token = " + client.getAuthToken() + ", url = " + client.getStorageURL());
        return context;
    }

}
