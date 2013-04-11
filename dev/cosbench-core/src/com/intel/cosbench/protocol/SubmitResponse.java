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
 * The response for request to sumit workload to drivers.
 * 
 * @author ywang19, qzheng7
 *
 */
public class SubmitResponse extends Response {

    private String id; /* mission id */

    public SubmitResponse() {
        /* empty */
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
