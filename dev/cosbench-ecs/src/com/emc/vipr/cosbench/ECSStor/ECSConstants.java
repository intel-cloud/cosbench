/*
 * Copyright 2014-2016 EMC Corporation. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.emc.vipr.cosbench.ECSStor;

/**
 * Constants for use with EMC ECS Storage API plugin
 * for COSBench.
 *
 * The EMC ECS plugin requires at least access key and
 * secret key to be user-defined. If unspecified, ECS
 * endpoint will default to s3.amazonaws.com with a connection
 * timeout of 30000ms; all other values are empty by default.
 */
public interface ECSConstants {

    // Connection Timeout Control
    public static final String CONN_TIMEOUT_KEY = "timeout";
    public static final String CONN_TIMEOUT_DEFAULT = "30000";

    // Read Timeout Control; default is same as connection timeout
    public static final String READ_TIMEOUT_KEY = "read_timeout";

    // ECS Endpoint Constants
    public static final String ENDPOINT_KEY = "endpoint";
    public static final String ENDPOINT_DEFAULT = "http://s3.amazonaws.com";

    // ECS Access Key
    public static final String AUTH_USERNAME_KEY = "accesskey";
    public static final String AUTH_USERNAME_DEFAULT = "";

    // ECS Secret Key
    public static final String AUTH_PASSWORD_KEY = "secretkey";
    public static final String AUTH_PASSWORD_DEFAULT = "";

    // Proxy Port/Host Keys - currently unsupported
//    public static final String PROXY_HOST_KEY = "proxyhost";
//    public static final String PROXY_PORT_KEY = "proxyport";

    // Namespace Key
    public static final String NAMESPACE_KEY = "namespace";

    // Path Style Access Constants
    public static final String PATH_STYLE_ACCESS_KEY = "path_style_access";
    public static final boolean PATH_STYLE_ACCESS_DEFAULT = false;

    // HTTP Client to use: apache/java
    public static final String HTTP_CLIENT_KEY = "http_client";
    public static final String HTTP_CLIENT_DEFAULT = "apache";

    // Whether to use the smart client (incl. client-side load balancing)
    public static final String SMART_CLIENT_KEY = "use_smart_client";
    public static final boolean SMART_CLIENT_DEFAULT = true;
}