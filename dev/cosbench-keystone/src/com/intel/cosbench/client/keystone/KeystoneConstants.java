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

public interface KeystoneConstants {

    // --------------------------------------------------------------------------
    // KEYSTONE
    // --------------------------------------------------------------------------
    String AUTH_URL_KEY = "auth_url";
    String AUTH_URL_ALTKEY = "url";
    String URL_DEFAULT = "http://127.0.0.1:5000/v2.0";

    // --------------------------------------------------------------------------
    // CONNECTION
    // --------------------------------------------------------------------------
    String CONN_TIMEOUT_KEY = "timeout";
    Integer CONN_TIMEOUT_DEFAULT = 10 * 1000;

    // --------------------------------------------------------------------------
    // CONTEXT NEEDS FROM AUTH MODULE
    // --------------------------------------------------------------------------
    String AUTH_TOKEN_KEY = "token";
    String STORAGE_URL_KEY = "storage_url";

    // --------------------------------------------------------------------------
    // AUTHENTICATION
    // --------------------------------------------------------------------------
    String AUTH_USERNAME_KEY = "username";
    String AUTH_USERNAME_DEFAULT = "";

    String AUTH_PASSWORD_KEY = "password";
    String AUTH_PASSWORD_DEFAULT = "";

    String AUTH_USERTOKEN_KEY = "usertoken";
    String AUTH_USERTOKEN_DEFAULT = "";

    String AUTH_TENANT_ID_KEY = "tenant_id";
    String AUTH_TENANT_ID_DEFAULT = "";

    String AUTH_TENANT_NAME_KEY = "tenant_name";
    String AUTH_TENANT_NAME_ALTKEY = "tenname";
    String AUTH_TENANT_NAME_DEFAULT = "";

    String AUTH_SERVICE_KEY = "service";
    String AUTH_SERVICE_DEFAULT = "swift";
    
    String AUTH_REGION_KEY = "region";
    String AUTH_REGION_DEFAULT = "regionOne";
}
