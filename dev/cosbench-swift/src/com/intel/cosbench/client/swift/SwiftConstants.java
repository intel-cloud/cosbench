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

package com.intel.cosbench.client.swift;

public interface SwiftConstants {

    // --------------------------------------------------------------------------
    // CONNECTION
    // --------------------------------------------------------------------------

    String CONN_TIMEOUT_KEY = "timeout";

    Integer CONN_TIMEOUT_DEFAULT = 30000;

    // --------------------------------------------------------------------------
    // CONTEXT NEEDS FROM AUTH MODULE
    // --------------------------------------------------------------------------

    String AUTH_TOKEN_KEY = "token";
    String AUTH_TOKEN_DEFAULT = "AUTH_xxx";

    String STORAGE_URL_KEY = "storage_url";
    String STORAGE_URL_DEFAULT = "http://127.0.0.1:8080/auth/v1.0";
    
    String POLICY_KEY = "policy";
    String POLICY_DEFAULT = null;
    
    String TRANSFER_RATE = "transfer_rate";
    Integer TRANSFER_RATE_DEFAULT = 50000000;
    
    // --------------------------------------------------------------------------
    // Swift RESTful API
    // --------------------------------------------------------------------------

    String X_STORAGE_URL = "X-Storage-Url";

    String X_AUTH_TOKEN = "X-Auth-Token";
    
    String X_STORAGE_POLICY = "X-Storage-Policy";

    String X_CONTAINER_OBJECT_COUNT = "X-Container-Object-Count";

    String X_CONTAINER_BYTES_USED = "X-Container-Bytes-Used";

    String X_ACCOUNT_CONTAINER_COUNT = "X-Account-Container-Count";

    String X_ACCOUNT_BYTES_USED = "X-Account-Bytes-Used";
    
    String X_TRANSFER_RATE = "X-Transfer-Rate";

}
