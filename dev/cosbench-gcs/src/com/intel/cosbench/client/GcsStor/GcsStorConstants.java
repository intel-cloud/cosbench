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
package com.intel.cosbench.client.GcsStor;

public interface GcsStorConstants {

    // --------------------------------------------------------------------------
    // CONNECTION
    // --------------------------------------------------------------------------

    String CONN_TIMEOUT_KEY = "timeout";
    int CONN_TIMEOUT_DEFAULT = 30000;

    // --------------------------------------------------------------------------
    // PROJECT_ID
    // --------------------------------------------------------------------------
    String PROJECT_ID = "projectid";
    String PROJECT_ID_DEFAULT = "xxxxx-xxxx";

    // --------------------------------------------------------------------------
    // JSON_KEY_FILE
    // --------------------------------------------------------------------------
    String JSON_KEY_FILE = "jsonkeyfile";
    String JSON_KEY_FILE_DEFAULT = "/path/to/json/key/file";

}
