package com.intel.cosbench.client.cdmi.base;

import org.apache.http.Header;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;


public class CdmiClientException extends HttpResponseException {
    private Header[] httpHeaders;
    private StatusLine httpStatusLine;

    public CdmiClientException(int code, String message) {
    	super(code, message);
    }
    
    public CdmiClientException(int code, String message, Header[] httpHeaders,
            StatusLine httpStatusLine) {
        super(code, message);
        this.httpHeaders = httpHeaders;
        this.httpStatusLine = httpStatusLine;
    }

    public Header[] getHttpHeaders() {
        return httpHeaders;
    }

    public StatusLine getHttpStatusLine() {
        return httpStatusLine;
    }
    
    private static final long serialVersionUID = 1L;

}
