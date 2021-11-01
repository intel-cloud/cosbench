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

package com.intel.cosbench.client.keystone;

import java.util.List;

import org.apache.http.client.HttpClient;

import com.intel.cosbench.client.keystone.KeystoneResponse.ServiceInfo;
import com.intel.cosbench.client.keystone.KeystoneResponse.ServiceInfo.Endpoint;
import com.intel.cosbench.client.keystone.KeystoneResponse.TokenInfo;
import com.intel.cosbench.client.keystone.KeystoneResponse.User;
import com.intel.cosbench.client.keystone.handler.*;

/**
 * A client for Openstack keystone authentication service. <br />
 * The client can be used to obtain a valid keystone token in exchange of
 * correct credentials. According to keystone, there are different ways to
 * achieve this:
 * 
 * <ol>
 * <li>Simple Authentication</li>
 * <p>
 * Supply the user name and password to get an authentication token. The token
 * may or may not be scoped, depending on whether the user has a default tenant
 * configured in keystone.
 * </p>
 * <li>Scoped Authentication</li>
 * <p>
 * Supply the user name and password along with a tenant info to get a token
 * that will be scoped with the specified tenant. The tenant info can be either
 * it's name or it's id.
 * </p>
 * <li>Token Authentication</li>
 * <p>
 * Supply an un-scoped keystone token obtained previously with a tenant info in
 * order to get a new token that will be scoped with the specified tenant.
 * </p>
 * </ol>
 * 
 * @author qzheng (qing.zheng@intel.com), osmboy (lei.lei@ostorage.com.cn)
 */
public class KeystoneClient {

    /* user info */
    private String username;
    private String password;

    /* tenant info */
    private String tenantName;
    
    /* domain info */
    private String domain;

    /* authentication handler */
    private AuthHandler handler;

    /* authentication response */
    private KeystoneResponse response;

    public KeystoneClient(HttpClient client, String url, String username,
            String password, String tenantName, String domain, int timeout) {
        this.username = username;
        this.password = password;
        this.tenantName = tenantName;
        this.domain = domain;
        this.handler = new HttpAuthHandler(url, timeout);
    }

    /**
     * Perform the authentication against a keystone service. Once the
     * authentication is successfully completed, the results can be retrieved
     * from the client instance.
     * 
     * @see KeystoneClient#getKeystoneTokenId()
     * @see KeystoneClient#getServiceUrl(String)
     */
    public void login() {
        this.response = null;
        KeystoneRequest request = initRequest();
        if (handler == null) {
            String e = "no handler is set in this keystone client";
            throw new IllegalStateException(e);
        }
        KeystoneResponse response = handler.POST(request);
        validateResponse(response);
        this.response = response;
    }

    private KeystoneRequest initRequest() {
        KeystoneRequest request = new KeystoneRequest();
        /* user info */
        if (this.username != null && this.password != null && this.domain != null) {
            request.addUserScope(this.username, this.password, this.domain);
        } else {
            String e = "no user info is detected in this keystone client";
            throw new IllegalStateException(e);
        }
        /* tenant info */
        if (this.tenantName != null && this.domain != null) {
        	request.addProjectScope(this.tenantName, this.domain);
        }  else {
        	String e = "no project info is detected in keystone scope";
            throw new IllegalStateException(e);
        }
        return request;
    }

    private void validateResponse(KeystoneResponse response) {
        TokenInfo info = response.getToken();
        if (info == null) {
            String e = "no access info is found in the auth response";
            throw new KeystoneResponseException(e);
        }
        User user = info.getUser();
        if (user == null) {
            String e = "no user info is found in the auth response";
            throw new KeystoneResponseException(e);
        }
        List<ServiceInfo> catalog = info.getCatalog();
        if (catalog == null) {
            String e = "no service catalog is found in the auth response";
            throw new KeystoneResponseException(e);
        }
        /* OK - all info is there */
    }

    public boolean isAuthenticated() {
        return (response != null);
    }

    // --------------------------------------------------------------------------
    // Getters and Setters
    // --------------------------------------------------------------------------

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
       
        this.domain = domain;
    }
    
    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public AuthHandler getHandler() {
        return handler;
    }

    public void setHandler(AuthHandler handler) {
        if (handler == null) {
            String e = "authentication handler cannot be null";
            throw new IllegalArgumentException(e);
        }
        this.handler = handler;
    }

    // --------------------------------------------------------------------------
    // Information Retrieval
    // --------------------------------------------------------------------------

    /**
     * Retrieve the keystone token id associated with this client. A keystone
     * token represents an authenticated user to keystone or other cloud
     * services that rely on keystone for authentication. This method should
     * only be called after the client has successfully performed an
     * authentication against the keystone service, <code>null</code> will be
     * returned otherwise.
     * 
     * @return the keystone token id
     */
    public String getKeystoneTokenId() {
        return getTokenInfo().getId();
    }

    /**
     * Retrieve the public URL of a cloud service identified by the given name.
     * This method should only be called after the client has successfully
     * performed an authentication against the keystone service,
     * <code>null</code> will be returned otherwise.
     * 
     * @param serviceName
     *            - the name identifying the service
     * @return the public URL of a cloud service
     */
    public String getServiceUrl(String serviceName) {
        ServiceInfo service = getServiceInfo(serviceName);
        if (service == null)
            return null;
        List<Endpoint> endpoints = service.getEndpoints();
        if (endpoints != null && endpoints.size() > 0)
            return endpoints.get(0).getUrl();
        return null;
    }

    /**
     * Retrieve the information regarding a cloud service identified by the
     * given name. This method should only be called after the client has
     * successfully performed an authentication against the keystone service,
     * <code>null</code> will be returned otherwise.
     * 
     * @param serviceName
     *            - the name identifying the service
     * @return the information regarding a cloud service
     */
    public ServiceInfo getServiceInfo(String serviceName) {
        List<ServiceInfo> catalog = getTokenInfo().getCatalog();
        for (ServiceInfo service : catalog)
            if (serviceName != null ? serviceName.equals(service.getName())
                    : service.getName() == null)
                return service;

        return null;
    }

    public User getUser() {
        return getTokenInfo().getUser();
    }

    private TokenInfo getTokenInfo() {
        return getResponse().getToken();
    }

    private KeystoneResponse getResponse() {
        return response;
    }

    public void dispose() {
        handler.dispose();
    }

    // --------------------------------------------------------------------------
    // End
    // --------------------------------------------------------------------------

}
