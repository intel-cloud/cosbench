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

/**
 * <code>
 *  { "access": ... }
 * </code>
 */
public class KeystoneResponse {

    private AccessInfo access;

    public AccessInfo getAccess() {
        return access;
    }

    public void setAccess(AccessInfo access) {
        this.access = access;
    }

    // --------------------------------------------------------------------------
    // AccessInfo
    // --------------------------------------------------------------------------

    /**
     * <code>
     *   { "user": ... , "token": ... , "serviceCatalog": [ ... ] }
     * </code>
     */
    public static class AccessInfo {

        private User user;
        private Token token;
        private List<ServiceInfo> serviceCatalog;

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public Token getToken() {
            return token;
        }

        public void setToken(Token token) {
            this.token = token;
        }

        public List<ServiceInfo> getServiceCatalog() {
            return serviceCatalog;
        }

        public void setServiceCatalog(List<ServiceInfo> serviceCatalog) {
            this.serviceCatalog = serviceCatalog;
        }

        // ----------------------------------------------------------------------
        // AccessInfo.User
        // ----------------------------------------------------------------------

        /**
         * <code>
         *   { "id": "?", "name": "?" }
         * </code>
         */
        public static class User {

            private String id;
            private String name;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

        }

        // ----------------------------------------------------------------------
        // AccessInfo.Token
        // ----------------------------------------------------------------------

        /**
         * <code>
         *   { "id": "?", "expires": "?" }
         * </code>
         */
        public static class Token {

            private String id;
            private String expires;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getExpires() {
                return expires;
            }

            public void setExpires(String expires) {
                this.expires = expires;
            }

        }

        // ----------------------------------------------------------------------
        // AccessInfo.ServiceInfo
        // ----------------------------------------------------------------------

        /**
         * <code>
         *   {"name": "?", "type": "?", "endpoints": [ ... ] }
         * </code>
         */
        public static class ServiceInfo {

            private String name;
            private String type;
            private List<Endpoint> endpoints;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public List<Endpoint> getEndpoints() {
                return endpoints;
            }

            public void setEndpoints(List<Endpoint> endpoints) {
                this.endpoints = endpoints;
            }

            // ------------------------------------------------------------------
            // AccessInfo.ServiceInfo.Endpoint
            // ------------------------------------------------------------------

            /**
             * <code>
             * { "adminURL": "?", "internalURL": "?", "publicURL": "?" }
             * </code>
             */
            public static class Endpoint {

                private String adminURL;
                private String internalURL;
                private String publicURL;
                private String region;

                public String getAdminURL() {
                    return adminURL;
                }

                public void setAdminURL(String adminURL) {
                    this.adminURL = adminURL;
                }

                public String getInternalURL() {
                    return internalURL;
                }

                public void setInternalURL(String internalURL) {
                    this.internalURL = internalURL;
                }

                public String getPublicURL() {
                    return publicURL;
                }

                public void setPublicURL(String publicURL) {
                    this.publicURL = publicURL;
                }

				public String getRegion() {
					return region;
				}

				public void setRegion(String region) {
					this.region = region;
				}
                
                

            }

        }

    }

    // --------------------------------------------------------------------------
    // End
    // --------------------------------------------------------------------------

}
