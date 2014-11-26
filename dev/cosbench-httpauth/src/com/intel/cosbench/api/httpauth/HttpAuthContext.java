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

import org.apache.http.client.HttpClient;

import com.intel.cosbench.api.context.AuthContext;

/**
 * This class encapsulates a HttpAuth specific AuthContext.
 * 
 * @author ywang19
 * 
 */
public class HttpAuthContext extends AuthContext {

	public HttpAuthContext(String auth_url, String username, String password, HttpClient client) {
		this.put(AUTH_URL_KEY, auth_url);
		this.put(AUTH_USERNAME_KEY, username);
		this.put(AUTH_PASSWORD_KEY, password);
		this.put(AUTH_CLIENT_KEY, client);
	}
	
	@Override
	public String getID(String meta) {
		StringBuilder builder = new StringBuilder();
		builder.append(getID());
		builder.append(";meta=");
		builder.append(meta);
		
		return builder.toString();
	}

	@Override
	public String getID() {
		StringBuilder builder = new StringBuilder();
		builder.append(AUTH_URL_KEY);
		builder.append("=");
		builder.append(this.getStr(AUTH_URL_KEY));
		builder.append(";");
		builder.append(AUTH_USERNAME_KEY);
		builder.append("=");
		builder.append(this.getStr(AUTH_USERNAME_KEY));
		builder.append(";");
		builder.append(AUTH_PASSWORD_KEY);
		builder.append("=");
		builder.append(this.getStr(AUTH_PASSWORD_KEY));
				
		return builder.toString();
	}

}
