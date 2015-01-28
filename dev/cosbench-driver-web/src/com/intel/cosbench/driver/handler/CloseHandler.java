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

package com.intel.cosbench.driver.handler;

import static com.intel.cosbench.model.MissionState.TERMINATED;
import static com.intel.cosbench.model.MissionState.FAILED;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.intel.cosbench.bench.Metrics;
import com.intel.cosbench.bench.Report;
import com.intel.cosbench.model.MissionInfo;
import com.intel.cosbench.model.TaskState;
import com.intel.cosbench.protocol.*;

public class CloseHandler extends MissionHandler {

    @Override
    protected Response process(MissionInfo info) {
        String id = info.getId();
        driver.close(id);
        if (info.getState().equals(TERMINATED))
            return new Response(false, "close failed");
        return getResponse(info);
    }

    private Response getResponse(MissionInfo info) {
        CloseResponse response = new CloseResponse();
        Report report = info.getReport();
        List<Metrics> wrReport = new ArrayList<Metrics>();
        for(Report wReport:info.getWorkerReports()){
        	for(Metrics metrics : wReport){
        		wrReport.add(metrics);
        	}
        }
        response.setWrReport(wrReport);
        response.setReport(Arrays.asList(report.getAllMetrics()));
		if (info.getState().equals(FAILED))
			response.setState(TaskState.FAILED);
		else
			response.setState(TaskState.ACCOMPLISHED);
        String log = null;
        try {
            log = info.getLogManager().getLogAsString();
        } catch (IOException e) {
            log = "[N/A]";
        }
        response.setErrorStatistics(info.getErrorStatistics().getErrorCodeAndNum());
        response.setDriverLog(log);
        
        return response;
    }

}
