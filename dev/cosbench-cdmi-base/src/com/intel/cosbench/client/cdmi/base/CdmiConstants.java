/**

Copyright 2013 Intel Corporation, All Rights Reserved.
Copyright 2019 OpenIO Corporation, All Rights Reserved.

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
package com.intel.cosbench.client.cdmi.base;

public interface CdmiConstants {

    // --------------------------------------------------------------------------
    // CONNECTION
    // --------------------------------------------------------------------------
    String TIMEOUT_KEY = "timeout";
    int TIMEOUT_DEFAULT = 30000;

    String ROOT_PATH_KEY = "root_path";
    String ROOT_PATH_DEFAULT = "";

    String RAISE_DELETE_ERRORS_KEY = "raise_delete_errors";
    Boolean RAISE_DELETE_ERRORS_DEFAULT = false;

    String CUSTOM_HEADERS_KEY = "custom_headers";
    String CUSTOM_HEADERS_DEFAULT = "";

    String CDMI_CONTENT_TYPE_KEY = "type";
    String CDMI_CONTENT_TYPE_DEFAULT = "cdmi";

    // --------------------------------------------------------------------------
    // CONNECTION
    // --------------------------------------------------------------------------
    String CONN_TIMEOUT_KEY = "timeout";
    Integer CONN_TIMEOUT_DEFAULT = 30000;

    // --------------------------------------------------------------------------
    // CONTEXT NEEDS FROM AUTH MODULE
    // --------------------------------------------------------------------------
    String AUTH_CLIENT_KEY = "client"; // optional, for http authencation
    String AUTH_TOKEN_KEY = "token";  // optional, for token-based authentication
    String STORAGE_URL_KEY = "storage_url";
}
