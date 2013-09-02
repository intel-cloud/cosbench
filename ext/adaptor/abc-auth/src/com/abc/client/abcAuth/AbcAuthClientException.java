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

package com.abc.client.abcAuth;

import org.apache.http.Header;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;


public class AbcAuthClientException extends HttpResponseException {
    private Header[] httpHeaders;
    private StatusLine httpStatusLine;

    public AbcAuthClientException(int code, String message) {
    	super(code, message);
    }
    
    public AbcAuthClientException(int code, String message, Header[] httpHeaders,
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
