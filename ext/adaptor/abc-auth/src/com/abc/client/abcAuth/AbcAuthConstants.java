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

package com.abc.client.abcAuth;

public interface AbcAuthConstants {

    // --------------------------------------------------------------------------
    // CONNECTION
    // --------------------------------------------------------------------------

    String CONN_TIMEOUT_KEY = "timeout";
    int CONN_TIMEOUT_DEFAULT = 30000;

    // --------------------------------------------------------------------------
    // AUTH
    // --------------------------------------------------------------------------

    String AUTH_URL_KEY = "auth_url";
    String URL_DEFAULT = "http://127.0.0.1:8080/auth/v1.0"; 

    // --------------------------------------------------------------------------
    // AUTHENTICATION
    // --------------------------------------------------------------------------

    String AUTH_USERNAME_KEY = "username";
    String AUTH_USERNAME_DEFAULT = "";
    
    String AUTH_PASSWORD_KEY = "password";
    String AUTH_PASSWORD_DEFAULT = "";

    // --------------------------------------------------------------------------
    // CONTEXT NEEDS FROM AUTH MODULE
    // --------------------------------------------------------------------------
    String AUTH_TOKEN_KEY = "token";

    // --------------------------------------------------------------------------
    // Storage RESTful API
    // --------------------------------------------------------------------------
    String X_STORAGE_USER = "X-Auth-User";
    String X_STORAGE_PASS = "X-Auth-Key";
    String X_AUTH_TOKEN = "X-Auth-Token";    

}
