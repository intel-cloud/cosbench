/**

Copyright 2021-2022 eHualu Corporation, All Rights Reserved.

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
package com.intel.cosbench.client.ehualu;


public interface EssConstants {
    // --------------------------------------------------------------------------
    // CONNECTION
    // --------------------------------------------------------------------------
    String CONN_TIMEOUT_KEY = "timeout";
    int CONN_TIMEOUT_DEFAULT = 30000;
    
    // --------------------------------------------------------------------------
    // ENDPOINT
    // --------------------------------------------------------------------------
    String ENDPOINT_KEY = "endpoint";
    String ENDPOINT_DEFAULT = "http://s3.amazonaws.com";

    // --------------------------------------------------------------------------
    // AUTHENTICATION
    // --------------------------------------------------------------------------
    String AUTH_USERNAME_KEY = "accesskey";
    String AUTH_USERNAME_DEFAULT = "";

    String AUTH_PASSWORD_KEY = "secretkey";
    String AUTH_PASSWORD_DEFAULT = "";

    // --------------------------------------------------------------------------
    // CLIENT CONFIGURATION
    // --------------------------------------------------------------------------
    String PROXY_HOST_KEY = "proxyhost";
    String PROXY_PORT_KEY = "proxyport";

    // MAX CONNECTIONS DEFAULT
    // --------------------------------------------------------------------------
    String MAX_CONNECTIONS = "max_connections";
    int MAX_CONNECTIONS_DEFAULT = 50;

    // --------------------------------------------------------------------------
    // PATH STYLE ACCESS
    // --------------------------------------------------------------------------
    String PATH_STYLE_ACCESS_KEY = "path_style_access";
    boolean PATH_STYLE_ACCESS_DEFAULT = false;

    // --------------------------------------------------------------------------
    // CONTEXT NEEDS FROM AUTH MODULE
    // --------------------------------------------------------------------------
    String S3CLIENT_KEY = "s3client";
    
    // --------------------------------------------------------------------------
    // 2020.11.26, if true, will verify ssl.
    // NO VERIFY SSL
    // --------------------------------------------------------------------------
    String NO_VERIFY_SSL_KEY = "no_verify_ssl";
    boolean NO_VERIFY_SSL_DEFAULT = false;
    
    // --------------------------------------------------------------------------
    // 2021.2.7
    // StorageClass
    // --------------------------------------------------------------------------
    String STORAGE_CLASS_KEY = "storage_class";
    String STORAGE_CLASS_DEFAULT = "STANDARD";
    
    // --------------------------------------------------------------------------
    // 2021.7.11
    // RestoreDays
    // --------------------------------------------------------------------------
    String RESTORE_DAYS_KEY = "restore_days";
    int RESTORE_DAYS_DEFAULT = 1;
    
    // --------------------------------------------------------------------------
    // 2021.8.3, default: 5MiB
    // PartSize for Multipart upload.
    // --------------------------------------------------------------------------
    String PART_SIZE_KEY = "part_size";
    long PART_SIZE_DEFAULT = 5 * 1024 * 1024; // 5MiB
    
    // --------------------------------------------------------------------------
    // 2022.02.03, add region
    // Default region is US-EAST-1.
    // --------------------------------------------------------------------------
    String REGION_KEY = "aws_region";
    String REGION_DEFAULT = "us-east-1";

}
