/**

Copyright 2013 Intel Corporation, All Rights Reserved.
Copyright 2019 OpenIO Corporation, All Rights Reserved.

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
package com.intel.cosbench.controller.model;

import java.util.HashMap;

public class ErrorSummary {
    private HashMap<String, Integer> errorCodeAndNum;
    public ErrorSummary(){
        errorCodeAndNum = new HashMap<String, Integer>();
    }
    public ErrorSummary(HashMap<String, Integer> errors){
        errorCodeAndNum = errors;
    }
    public HashMap<String, Integer> getErrorCodeAndNum() {
        return errorCodeAndNum;
    }
    public void assignEntry(String key, Integer value){
        errorCodeAndNum.put(key, value);
    }
    public boolean contains(String key){
        return errorCodeAndNum.containsKey(key);
    }


}
