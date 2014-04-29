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
