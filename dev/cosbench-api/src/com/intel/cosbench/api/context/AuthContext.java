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

package com.intel.cosbench.api.context;

/**
 * @author ywang19
 *
 */
public abstract class AuthContext extends Context {

	/**
	 * Generate unique ID for the authentication context with attached metadata, normally, it will 
	 * extract some key-value pairs from the context to construct the ID.
	 * 
	 * @param meta
	 * @return an unique ID
	 */
	public abstract String getID(String meta) ;
	
	
	/**
	 * Generate unique ID for authentication context, normally, it will extract some key-value pairs 
	 * from the context to construct the ID.
	 * 
	 * @return an unique ID
	 */
	public abstract String getID();
	
}
