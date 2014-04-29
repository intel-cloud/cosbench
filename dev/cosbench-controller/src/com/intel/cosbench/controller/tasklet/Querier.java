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

import static com.intel.cosbench.model.TaskState.FINISHED;

import java.util.Date;

import com.intel.cosbench.bench.*;
import com.intel.cosbench.controller.model.TaskContext;
import com.intel.cosbench.protocol.QueryResponse;
import com.intel.cosbench.service.CancelledException;

/**
 * The class encapsulates how to handle query request/response, internally, it
 * issues command to query driver to get performance snapshot.
 * 
 * @author ywang19, qzheng7
 * 
 */
class Querier extends AbstractCommandTasklet<QueryResponse> {

    public Querier(TaskContext context) {
        super(context, QueryResponse.class);
    }

    @Override
    protected void execute() {
        String id = context.getMissionId();
        do {
            sleep();
            try{
            	issueCommand("query", id);
            }catch(Exception tle) {
            	LOGGER.warn("some unexpected exception occurs when ping drivers, but it's ignorable.", tle);
            }
        } while (!context.getState().equals(FINISHED));
    }

    private void sleep() {
        long seconds = context.getInterval();
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            throw new CancelledException(); // task cancelled
        }
    }

    @Override
    protected void handleResponse(QueryResponse response) {
    	if (response == null) {
    		LOGGER.warn("no response gets from driver");
    		return;
    	}
    	
        if (!response.isRunning())
            context.setState(FINISHED); // stop querying
        Date time = response.getTime();
        Report report = new Report();
        for (Metrics metrics : response.getReport())
            report.addMetrics(metrics);
        Snapshot snapshot = new Snapshot(report, time);
        snapshot.setVersion(response.getVersion());
        snapshot.setMinVersion(response.getMinVersion());
        snapshot.setMaxVersion(response.getMaxVersion());
        context.setSnapshot(snapshot);
    }

}
