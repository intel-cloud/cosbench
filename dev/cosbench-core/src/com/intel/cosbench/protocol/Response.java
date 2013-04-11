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

package com.intel.cosbench.protocol;

/**
 * The base class encapsulates HTTP response from Drivers.
 * 
 * @author ywang19, qzheng7
 *
 */
public class Response {

    private int code;
    private boolean succ;
    private String error;

    public Response() {
        this.code = 200;
        this.succ = true;
    }

    public Response(int code) {
        this.code = code;
        this.succ = false;
    }
    
    public Response(int code, String error) {
        this.code = code;
        this.succ = false;
        this.error = error;
    }

    public Response(boolean succ, String error) {
        this.code = 200;
        this.succ = succ;
        this.error = error;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isSucc() {
        return succ;
    }

    public void setSucc(boolean succ) {
        this.succ = succ;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

}
