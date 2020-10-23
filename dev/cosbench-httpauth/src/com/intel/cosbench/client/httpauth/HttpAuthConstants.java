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
    String STORAGE_URL_KEY = "storage_url";

    // --------------------------------------------------------------------------
    // AUTHENTICATION
    // --------------------------------------------------------------------------
    String AUTH_URL_KEY = "auth_url";
    String AUTH_URL_DEFAULT = "http://127.0.0.1:80";
    String AUTH_USERNAME_KEY = "username";
    String AUTH_USERNAME_DEFAULT = "";
    String AUTH_PASSWORD_KEY = "password";
    String AUTH_PASSWORD_DEFAULT = "";

}
