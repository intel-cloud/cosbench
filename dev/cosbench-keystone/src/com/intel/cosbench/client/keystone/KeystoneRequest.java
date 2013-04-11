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

import com.intel.cosbench.client.keystone.KeystoneRequest.AuthInfo.Credentials;
import com.intel.cosbench.client.keystone.KeystoneRequest.AuthInfo.Token;

/**
 * The request that will be used when obtaining a keystone token from the
 * keystone service. It contains information including the username, password,
 * tenant name, tenant id and user token. Note that not all information is
 * required for a request to be accepted. Some are even conflicting with others.
 * This class is specially structured in a way that is compatible with the
 * interface provided by the keystone service. Please refer to the keystone
 * documents for more detailed information. <br />
 * 
 * <code>
 *   {"auth": ... }
 * </code>
 * 
 * @author qzheng
 */
public class KeystoneRequest {

    private AuthInfo auth;

    public KeystoneRequest() {
        AuthInfo auth = new AuthInfo();
        this.auth = auth;
    }

    public AuthInfo getAuth() {
        return auth;
    }

    public void setAuth(AuthInfo auth) {
        this.auth = auth;
    }

    public void addCredentials(String username, String password) {
        Credentials credentials = new Credentials();
        credentials.setUsername(username);
        credentials.setPassword(password);
        auth.setPasswordCredentials(credentials);
    }

    public void addUserToken(String id) {
        Token token = new Token();
        token.setId(id);
        auth.setToken(token);
    }

    public void addTenantId(String tenantId) {
        auth.setTenantId(tenantId);
    }

    public void addTenantName(String tenantName) {
        auth.setTenantName(tenantName);
    }

    // --------------------------------------------------------------------------
    // AuthInfo
    // --------------------------------------------------------------------------

    /**
     * The authentication information holding either the password credentials or
     * the token id. This class is specially structured in a way that is
     * compatible with the interface provided by the keystone service.<br />
     * 
     * <code>
     *   {"passwordCredentials": ... , "token": ... ,
     *    "tenantId": "?", "tenantName": "?"}
     * </code>
     * 
     * @author qzheng
     */
    public static class AuthInfo {

        private Credentials passwordCredentials;
        private Token token;
        private String tenantId;
        private String tenantName;

        public Credentials getPasswordCredentials() {
            return passwordCredentials;
        }

        public void setPasswordCredentials(Credentials passwordCredentials) {
            this.passwordCredentials = passwordCredentials;
        }

        public Token getToken() {
            return token;
        }

        public void setToken(Token token) {
            this.token = token;
        }

        public String getTenantId() {
            return tenantId;
        }

        public void setTenantId(String tenantId) {
            this.tenantId = tenantId;
        }

        public String getTenantName() {
            return tenantName;
        }

        public void setTenantName(String tenantName) {
            this.tenantName = tenantName;
        }

        // ----------------------------------------------------------------------
        // AuthInfo.Credentials
        // ----------------------------------------------------------------------

        /**
         * The credentials comprised of both the username and the password. This
         * class is specially structured in a way that is compatible with the
         * interface provided by the keystone service.<br />
         * 
         * <code>
         *   {"username": "?", "password": "?"} 
         * </code>
         * 
         * @author qzheng
         */
        public static class Credentials {

            private String username;
            private String password;

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

        }

        // ----------------------------------------------------------------------
        // AuthInfo.Token
        // ----------------------------------------------------------------------

        /**
         * The token meta data identified by its id. This class is specially
         * structured in a way that is compatible with the interface provided by
         * the keystone service.<br />
         * 
         * <code>
         *   {"id": "?"} 
         * </code>
         * 
         * @author qzheng
         */
        public static class Token {

            private String id;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

        }

    }

    // --------------------------------------------------------------------------
    // End
    // --------------------------------------------------------------------------

}
