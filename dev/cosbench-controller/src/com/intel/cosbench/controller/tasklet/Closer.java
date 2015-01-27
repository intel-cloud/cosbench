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

import java.util.List;

import com.intel.cosbench.bench.*;
import com.intel.cosbench.controller.model.TaskContext;
import com.intel.cosbench.protocol.CloseResponse;

/**
 * The class encapsulates how to handle close request/response, internally, it
 * issues command to close the workload on driver, and get report and log from
 * driver.
 * 
 * @author ywang19, qzheng7
 * 
 */
class Closer extends AbstractCommandTasklet<CloseResponse> {

    public Closer(TaskContext context) {
        super(context, CloseResponse.class);
    }

    @Override
    protected void execute() {
        String id = context.getMissionId();
        issueCommand("close", id);
        try {
            closeHttpClient();
        } catch (Exception e) {
            LOGGER.error("unexpected exception", e);
        }
    }

    @Override
    protected void handleResponse(CloseResponse response) {
        Report report = new Report();
        for (Metrics metrics : response.getReport())
            report.addMetrics(metrics);
        context.setReport(report);
        context.setLog(response.getDriverLog());
        context.setState(response.getState());
        context.setErrorStatistics(response.getErrorStatistics());
       
        for(Metrics metrics : response.getWrReport()){
        	context.getWrReport().add(metrics);
        }
    }
}
