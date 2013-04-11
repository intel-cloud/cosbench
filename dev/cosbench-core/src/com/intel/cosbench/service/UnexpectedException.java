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

package com.intel.cosbench.service;

/**
 * This exception is used to adapt those exceptions that should never occur and
 * thus unexpected to runtime exceptions so that we can IGNORE them in an
 * elegant manner, while normally they will eventually be caught and LOGGED in a
 * top level place. Developers should consult logs for details of these
 * exceptions.
 * 
 * @author ywang19, qzheng7
 * 
 */
public class UnexpectedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UnexpectedException(Throwable cause) {
        super(cause);
    }

    public UnexpectedException(String message, Throwable cause) {
        super(message, cause);
    }

}
