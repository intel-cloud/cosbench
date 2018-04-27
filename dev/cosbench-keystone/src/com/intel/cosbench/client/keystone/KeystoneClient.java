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

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;

import com.intel.cosbench.client.keystone.KeystoneResponse.AccessInfo;
import com.intel.cosbench.client.keystone.KeystoneResponse.AccessInfo.ServiceInfo;
import com.intel.cosbench.client.keystone.KeystoneResponse.AccessInfo.ServiceInfo.Endpoint;
import com.intel.cosbench.client.keystone.KeystoneResponse.AccessInfo.Token;
import com.intel.cosbench.client.keystone.KeystoneResponse.AccessInfo.User;
import com.intel.cosbench.client.keystone.handler.*;
import com.intel.cosbench.log.Logger;

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
 * @author ywang19 
 * @author qzheng
 */
public class KeystoneClient {
	
	private Logger logger;
	
    /* user info */
    private String username;
    private String password;
    private String userToken;
    
    /* tenant info */
    private String tenantId;
    private String tenantName;
    
    /*targe region*/
    private String region;
    /* authentication handler */
    private AuthHandler handler;

    /* authentication response */
    private KeystoneResponse response;

    public KeystoneClient(Logger logger, HttpClient client, String url, String username,
            String password, String tenantName, int timeout) {
    	this.logger = logger;
        this.username = username;
        this.password = password;
        this.tenantName = tenantName;
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
        if (this.username != null && this.password != null) {
            request.addCredentials(this.username, this.password);
        } else if (this.userToken != null) {
            request.addUserToken(this.userToken);
        } else {
            String e = "no user info is detected in this keystone client";
            throw new IllegalStateException(e);
        }
        /* tenant info */
        if (this.tenantId != null) {
            request.addTenantId(this.tenantId);
        } else if (this.tenantName != null) {
            request.addTenantName(this.tenantName);
        }
        return request;
    }

    private void validateResponse(KeystoneResponse response) {
        AccessInfo info = response.getAccess();
        if (info == null) {
            String e = "no access info is found in the auth response";
            throw new KeystoneResponseException(e);
        }
        User user = info.getUser();
        if (user == null) {
            String e = "no user info is found in the auth response";
            throw new KeystoneResponseException(e);
        }
        Token token = info.getToken();
        if (token == null) {
            String e = "no token info is found in the auth response";
            throw new KeystoneResponseException(e);
        }
        List<ServiceInfo> catalog = info.getServiceCatalog();
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
        if (username != null) {
            if (this.userToken != null) {
                String e = "cannot set username and userToken simultaneously";
                throw new IllegalStateException(e);
            }
            if (username.isEmpty()) {
                String e = "username cannot be empty";
                throw new IllegalArgumentException(e);
            }
        }
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password != null) {
            if (this.userToken != null) {
                String e = "cannot set password and userToken simultaneously";
                throw new IllegalStateException(e);
            }
            if (password.isEmpty()) {
                String e = "password cannot be empty";
                throw new IllegalArgumentException(e);
            }
        }
        this.password = password;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        if (userToken != null) {
            if (this.username != null) {
                String e = "cannot set usernmae and userToken simultaneously";
                throw new IllegalStateException(e);
            }
            if (this.password != null) {
                String e = "cannot set password and userToken simultaneously";
                throw new IllegalStateException(e);
            }
            if (userToken.isEmpty()) {
                String e = "userToken cannot be empty";
                throw new IllegalArgumentException(e);
            }
        }
        this.userToken = userToken;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        if (tenantId != null) {
            if (this.tenantName != null) {
                String e = "cannot set tenant id and name simultaneously";
                throw new IllegalStateException(e);
            }
            if (tenantId.isEmpty()) {
                String e = "tenant id cannot be empty";
                throw new IllegalArgumentException(e);
            }
        }
        this.tenantId = tenantId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        if (tenantName != null) {
            if (this.tenantId != null) {
                String e = "cannot set tenant id and name simultaneously";
                throw new IllegalStateException(e);
            }
            if (tenantName.isEmpty()) {
                String e = "tenant name cannot be empty";
                throw new IllegalArgumentException(e);
            }
        }
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
        return getToken().getId();
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
    public String getServiceUrl(String serviceName, String region) {
        ServiceInfo service = getServiceInfo(serviceName);
        if (service == null)
            return null;
        List<Endpoint> endpoints = service.getEndpoints();
        
        if (endpoints == null || endpoints.size() == 0)
        {
        	logger.error("no endpoints return from keystone");
        	return null;
        }
      
        List<String> regions = new ArrayList<String>();
	   	for (Endpoint endpoint : endpoints) {
    		String the_region = endpoint.getRegion();
    		if(the_region != null) {
    			regions.add(the_region);
    		}
	   	}
	   	
        if (region == null || region.isEmpty()) { // no region assigned, will use the first one.

    	   	logger.warn("Below regions are returned from keystone : " + regions.toString() + 
    	   			", but no expected region assigned in your configuration, so the first region will be used.");
    	   	return endpoints.get(0).getPublicURL();
        }

        int idx = -1;
	   	if((idx=regions.indexOf(region)) >= 0) {
	   		return endpoints.get(idx).getPublicURL();
		}
        
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
        List<ServiceInfo> catalog = getAccessInfo().getServiceCatalog();
        for (ServiceInfo service : catalog)
            if (serviceName != null ? serviceName.equals(service.getName())
                    : service.getName() == null)
                return service;
        
        List<String> services = new ArrayList<String>();
        for (ServiceInfo service : catalog)
        	services.add(service.getName());
        
        logger.error("no designated service [" + serviceName + "] found, but only those services returned: " + services.toString());
        
        return null;
    }

    public User getUser() {
        return getAccessInfo().getUser();
    }

    public Token getToken() {
        return getAccessInfo().getToken();
    }

    private AccessInfo getAccessInfo() {
        return getResponse().getAccess();
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
