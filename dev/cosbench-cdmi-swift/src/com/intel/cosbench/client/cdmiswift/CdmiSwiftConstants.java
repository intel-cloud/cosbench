package com.intel.cosbench.client.cdmiswift;

public interface CdmiSwiftConstants {

    // --------------------------------------------------------------------------
    // CONNECTION
    // --------------------------------------------------------------------------
    String TIMEOUT_KEY = "timeout";
    int TIMEOUT_DEFAULT = 30000;

    String ROOT_PATH_KEY = "root_path";
    String ROOT_PATH_DEFAULT = "/cdmi";
    
    String STORAGE_URL_KEY = "storage_url";
    String STORAGE_URL_DEFAULT = "http://127.0.0.1:8080/cdmi"; // default storage url for CDMI RI Server
    
    // --------------------------------------------------------------------------
    // CONTEXT NEEDS FROM AUTH MODULE
    // --------------------------------------------------------------------------
    String AUTH_TOKEN_KEY = "token";
    
    // --------------------------------------------------------------------------
    // Storage RESTful API
    // --------------------------------------------------------------------------
    String X_AUTH_TOKEN = "X-Auth-Token";
}
