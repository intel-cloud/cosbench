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
@author osmboy (lei.lei@ostorage.com.cn)
*/ 

package com.intel.cosbench.client.keystone;

import java.util.List;


public class KeystoneResponse {

    private TokenInfo token;

    public TokenInfo getToken() {
        return token;
    }

    public void setToken(TokenInfo token) {
        this.token = token;
    }

    public static class TokenInfo {

        private String id;
        private String name;
        private User user;
        private List<ServiceInfo> catalog;
        
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
	public List<ServiceInfo> getCatalog() {
	    return catalog;
	}
	public void setCatalog(List<ServiceInfo> catalog) {
	    this.catalog = catalog;
	}
	public User getUser() {
	    return user;
	}
	public void setUser(User user) {
	    this.user = user;
	}


    }
   
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
            
        public static class Endpoint {
            private String url;
            private String region;
	    public String getUrl() {
		return url;
	    }
	    public void setUrl(String url) {
		this.url = url;
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
