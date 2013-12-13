package com.intel.cosbench.client.cdmiswift;

import org.apache.http.Header;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;


public class CdmiSwiftClientException extends HttpResponseException {
    private Header[] httpHeaders;
    private StatusLine httpStatusLine;

    public CdmiSwiftClientException(int code, String message) {
    	super(code, message);
    }
    
    public CdmiSwiftClientException(int code, String message, Header[] httpHeaders,
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
