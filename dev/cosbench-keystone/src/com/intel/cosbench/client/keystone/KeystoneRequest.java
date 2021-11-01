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

import java.util.Collections;
import java.util.List;

import com.intel.cosbench.client.keystone.KeystoneRequest.AuthInfo.*;;

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
    
    public void addProjectScope(String tenantName, String id) {
	// TODO Auto-generated method stub
    	Scope scope = new Scope();
    	Project project = new Project();
    	Domain domain = new Domain();
    	domain.setId(id);
    	project.setDomain(domain);
    	project.setName(tenantName);
    	scope.setProject(project);
    	auth.setScope(scope);
    }

    public void addUserScope(String username, String password, String id) {
	// TODO Auto-generated method stub
	Identity identity = new Identity();
    	Password pwd = new Password();
	User user = new User();
	Domain domain = new Domain();
	domain.setId(id);
	user.setDomain(domain);
	user.setName(username);
	user.setPassword(password);
	pwd.setUser(user);
	identity.setPassword(pwd);
	identity.setMethods(Collections.singletonList("password"));
	auth.setIdentity(identity);
		
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
     *   { "auth": {
             "identity": {
               "methods": ["password"],
               "password": {
                 "user": {
                   "name": "admin",
                   "domain": { "id": "default" },
                   "password": "adminpwd"
                 }
               }
             },
             "scope": {
               "project": {
                 "name": "demo",
                 "domain": { "id": "default" }
               }
             }
           }
         }
     * </code>
     * 
     * @author qzheng
     */
    public static class AuthInfo {
    	
        private Identity identity;
        private Scope scope;
        
	public Identity getIdentity() {
	    return identity;
	}

	public void setIdentity(Identity identity) {
	    this.identity = identity;
	}

	public Scope getScope() {
	    return scope;
	}

	public void setScope(Scope scope) {
		this.scope = scope;
	}
		
	public static class Identity {
	    private List<String> methods;
	    private Password password;
			
	    public Password getPassword() {
	        return password;
	    }
			
	    public void setPassword(Password password) {
		this.password = password;
	    }

	    public List<String> getMethods() {
		return methods;
	    }

	    public void setMethods(List<String> methods) {
		this.methods = methods;
	    }

	}
		
        public static class Scope {
            private Project project;

	    public Project getProject() {
		return project;
	    }

	    public void setProject(Project project) {
		this.project = project;
	    }
        }
        
        public static class Project {
            private String name;
            private Domain domain;
        	
	    public String getName() {
		return name;
	    }
			
	    public void setName(String name) {
		this.name = name;
	    }
			
	    public Domain getDomain() {
		return domain;
	    }
			
	    public void setDomain(Domain domain) {
		this.domain = domain;
	    }
        }
        
        public static class Password {
            private User user;

	    public User getUser() {
		return user;
	    }

	    public void setUser(User user) {
		this.user = user;
	    }
        	
        }
        public static class Domain {
            private String id;

            public String getId() {
		return id;
	    }

	    public void setId(String id) {
		this.id = id;
	    }
        }
        public static class User {

            private String name;
            private Domain domain;
            private String password;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getPassword() {
                return password;
            }    
            
            public Domain getDomain() {
		return domain;
	    }

	    public void setDomain(Domain domain) {
		this.domain = domain;
	    }

	    public void setPassword(String password) {
                this.password = password;
            }

        }        
    }

}
