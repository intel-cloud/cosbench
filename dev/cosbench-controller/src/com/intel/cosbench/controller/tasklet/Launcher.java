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

package com.intel.cosbench.controller.tasklet;

import static com.intel.cosbench.model.TaskState.LAUNCHED;

import com.intel.cosbench.controller.model.TaskContext;
import com.intel.cosbench.protocol.LaunchResponse;

/**
 * The class encapsulates how to handle launch request/response, internally, it
 * issues command to start workload execution on driver.
 * 
 * @author ywang19, qzheng7
 * 
 */
class Launcher extends AbstractCommandTasklet<LaunchResponse> {

    public Launcher(TaskContext context) {
        super(context, LaunchResponse.class);
    }

    @Override
    protected void execute() {
        String id = context.getMissionId();
        issueCommand("launch", id);
        context.setState(LAUNCHED);
    }

    @Override
    protected void handleResponse(LaunchResponse response) {
        context.setInterval(response.getInterval());
    }

}
