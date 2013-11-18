/**

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

package com.scality.cosbench.client;

public interface SproxydConstants {

	String LOGGING_KEY = "logging";

	Boolean LOGGING_DEFAULT = Boolean.FALSE;

	// --------------------------------------------------------------------------
	// CONNECTION
	// --------------------------------------------------------------------------

	String CONN_TIMEOUT_KEY = "timeout";
	int CONN_TIMEOUT_DEFAULT = 30000;

	String BASE_PATH_KEY = "base_path";
	String BASE_PATH_DEFAULT = "/proxy/chord";

	String HOSTS_KEY = "hosts";
	String HOSTS_DEFAULT = "127.0.0.1";

	String PORT_KEY = "port";
	int PORT_DEFAULT = 81;

	String POOL_SIZE_KEY = "pool_size";
	/**
	 * MaxTotal,MaxPerRoute for the HTTP connection manager pool
	 */
	String POOL_SIZE_DEFAULT = "60,10";

}
