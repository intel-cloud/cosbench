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

package com.intel.cosbench.api.keystone;

import static com.intel.cosbench.client.keystone.KeystoneConstants.*;

import org.apache.http.client.HttpClient;

import com.intel.cosbench.api.auth.*;
import com.intel.cosbench.api.context.AuthContext;
import com.intel.cosbench.client.http.HttpClientUtil;
import com.intel.cosbench.client.keystone.*;
import com.intel.cosbench.config.Config;
import com.intel.cosbench.log.Logger;

/**
 * This class encapsulates an Openstack Keystone implementation for the
 * Auth-API.
 * 
 * @author ywang19, qzheng7, osmboy
 * 
 */
class KeystoneAuth extends NoneAuth {

    private KeystoneClient client;

    /* user info */
    private String url;
    private String username;
    private String password;

    /* tenant info */
    private String tenantName;

    /* domain info */
    private String domain;
    /* service info */
    private String service;

    /* connection setting */
    private int timeout;

    public KeystoneAuth() {
        /* empty */
    }

    @Override
    public void init(Config config, Logger logger) {
        super.init(config, logger);

        url = config.get(AUTH_URL_KEY, config.get(AUTH_URL_ALTKEY, URL_DEFAULT));
        username = config.get(AUTH_USERNAME_KEY, AUTH_USERNAME_DEFAULT);
        password = config.get(AUTH_PASSWORD_KEY, AUTH_PASSWORD_DEFAULT);
        tenantName = config.get(AUTH_TENANT_NAME_KEY, config.get(AUTH_TENANT_NAME_ALTKEY, AUTH_TENANT_NAME_DEFAULT));
        domain = config.get(AUTH_DOMAIN_KEY, AUTH_DOMAIN_DEFAULT);
        service = config.get(AUTH_SERVICE_KEY, AUTH_SERVICE_DEFAULT);
        timeout = config.getInt(CONN_TIMEOUT_KEY, CONN_TIMEOUT_DEFAULT);

        parms.put(AUTH_URL_KEY, url);
        parms.put(AUTH_USERNAME_KEY, username);
        parms.put(AUTH_PASSWORD_KEY, password);
        parms.put(AUTH_DOMAIN_KEY, domain);
        parms.put(AUTH_TENANT_NAME_KEY, tenantName);
        parms.put(AUTH_SERVICE_KEY, service);
        parms.put(CONN_TIMEOUT_KEY, timeout);

        logger.debug("using auth config: {}", parms);

        HttpClient httpClient = HttpClientUtil.createHttpClient(timeout);
        client = new KeystoneClient(httpClient, url, username, password,
                tenantName, domain, timeout);
        logger.debug("keystone client has been initialized");
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
        } catch (KeystoneTimeoutException te) {
            throw new AuthTimeoutException(te);
        } catch (KeystoneInterruptedException ie) {
            throw new AuthInterruptedException(ie);
        } catch (KeystoneAuthException ae) {
            throw new AuthBadException(ae);
        } catch (Exception e) {
            throw new AuthException(e);
        }
        return createContext();
    }

    private AuthContext createContext() {
//        AuthContext context = new DefaultAuthContext();
//        context.put(AUTH_TOKEN_KEY, client.getKeystoneTokenId());
//        context.put(STORAGE_URL_KEY, client.getServiceUrl(service));
//        return context;
        KeystoneAuthContext context = new KeystoneAuthContext(url, username, password, service, client.getKeystoneTokenId(), client.getServiceUrl(service));
        
        return context;
    }

}
