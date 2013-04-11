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

package com.intel.cosbench.api.mock;

public interface MockConstants {

    String AUTH_TOKEN_KEY = "token";

    String AUTH_TOKEN_DEFAULT = "token";

    String OBJECT_SIZE_KEY = "size";

    Integer OBJECT_SIZE_DEFAULT = 1000;

    String OP_DELAY_KEY = "delay";

    Long OP_DELAY_DEFAULT = 10L;

    String OP_ERRORS_KEY = "errors";

    Double OP_ERRORS_DEFAULT = 0D;

    String PRINTING_KEY = "printing";

    Boolean PRINTING_DEFAULT = Boolean.FALSE;

    String PROFILING_KEY = "doStats";

    Boolean PROFILING_DEFAULT = Boolean.FALSE;

}
