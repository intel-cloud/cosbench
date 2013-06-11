/*
 * LibradosConstants.java 27.05.2013
 * 
 * Copyright (c) 2013 1&1 Internet AG. All rights reserved.
 * 
 * $Id$
 */
package com.intel.cosbench.client.librados;

public interface LibradosConstants {
    // --------------------------------------------------------------------------
    // CONNECTION
    // --------------------------------------------------------------------------

    // String CONN_TIMEOUT_KEY = "timeout";
    // int CONN_TIMEOUT_DEFAULT = 30000;
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

    // String PROXY_HOST_KEY = "proxyhost";
    // String PROXY_PORT_KEY = "proxyport";

    // --------------------------------------------------------------------------
    // CONTEXT NEEDS FROM AUTH MODULE
    // --------------------------------------------------------------------------
    // String S3CLIENT_KEY = "s3client";
}
