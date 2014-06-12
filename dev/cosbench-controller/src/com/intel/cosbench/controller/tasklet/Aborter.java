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

import static com.intel.cosbench.model.TaskState.*;

import com.intel.cosbench.bench.Metrics;
import com.intel.cosbench.bench.Report;
import com.intel.cosbench.controller.model.TaskContext;
import com.intel.cosbench.protocol.AbortResponse;

/**
 * The class encapsulates how to handle abort request/response, internally, it
 * issues command to abort driver, and get log from driver.
 * 
 * @author ywang19, qzheng7
 * 
 */
class Aborter extends AbstractCommandTasklet<AbortResponse> {

    public Aborter(TaskContext context) {
        super(context, AbortResponse.class);
    }

    @Override
    protected void execute() {
        executeAbort();
        if (!context.getState().equals(ERROR))
            context.setState(ABORTED);
        else
            context.setState(TERMINATED);
    }

    private void executeAbort() {
        try {
            String id = context.getMissionId();
            try {
                issueCommand("abort", id);
            } catch (Exception e) {
                LOGGER.error("fail to abort driver", e);
            } finally {
                closeHttpClient();
            }
        } catch (Exception e) {
            LOGGER.error("unexpected exception", e);
        }
    }

    @Override
    protected void handleResponse(AbortResponse response) {
    	Report report = new Report();
        for (Metrics metrics : response.getReport())
            report.addMetrics(metrics);
        context.setReport(report);
        context.setLog(response.getDriverLog());
    }

}
