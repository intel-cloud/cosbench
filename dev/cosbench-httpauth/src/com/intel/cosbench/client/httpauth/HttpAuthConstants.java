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

package com.intel.cosbench.client.httpauth;

public interface HttpAuthConstants {

    // --------------------------------------------------------------------------
    // CONNECTION
    // --------------------------------------------------------------------------
    String CONN_TIMEOUT_KEY = "timeout";
    Integer CONN_TIMEOUT_DEFAULT = 30000;

    // --------------------------------------------------------------------------
    // CONTEXT NEEDS FROM AUTH MODULE
    // --------------------------------------------------------------------------
    String AUTH_CLIENT_KEY = "client";

    // --------------------------------------------------------------------------
    // AUTHENTICATION
    // --------------------------------------------------------------------------
    String AUTH_PROTOCOL_KEY = "protocol";
    String AUTH_PROTOCOL_DEFAULT = "http";
    String AUTH_HOST_KEY = "host";
    String AUTH_HOST_DEFAULT = "localhost";
    String AUTH_PORT_KEY = "port";
    int AUTH_PORT_DEFAULT = 80;
    String AUTH_USERNAME_KEY = "username";
    String AUTH_USERNAME_DEFAULT = "";
    String AUTH_PASSWORD_KEY = "password";
    String AUTH_PASSWORD_DEFAULT = "";

}
