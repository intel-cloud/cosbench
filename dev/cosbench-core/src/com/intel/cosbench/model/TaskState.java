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

package com.intel.cosbench.model;

import java.util.*;

public enum TaskState {

    CREATED,

    BOOTED,

    SUBMITTED,

    AUTHED,

    LAUNCHED,

    FINISHED,
    
    FAILED,

    ACCOMPLISHED,

    ABORTED,

    TERMINATED,

    ERROR,

    INTERRUPTED;

    public static Set<TaskState> FINAL_STATES = null;

    static {
        FINAL_STATES = new HashSet<TaskState>();
        FINAL_STATES.add(FAILED);
        FINAL_STATES.add(ACCOMPLISHED);
        FINAL_STATES.add(TERMINATED);
        FINAL_STATES.add(ABORTED);
    }

    public static boolean isStopped(TaskState state) {
        return FINAL_STATES.contains(state);
    }

}
