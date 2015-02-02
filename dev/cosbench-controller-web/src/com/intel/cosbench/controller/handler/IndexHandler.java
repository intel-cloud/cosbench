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

package com.intel.cosbench.controller.handler;

import javax.servlet.http.*;

import com.intel.cosbench.model.*;
import com.intel.cosbench.service.ControllerService;

public class IndexHandler extends AbstractClientHandler {

    private ControllerService controller;

    public void setController(ControllerService controller) {
        this.controller = controller;
    }

    @Override
    protected String process(HttpServletRequest req, HttpServletResponse res)
            throws Exception {
        StringBuilder buffer = new StringBuilder();
        DriverInfo[] drivers = controller.getControllerInfo().getDriverInfos();
        buffer.append("Drivers:").append('\n');
        for (DriverInfo driver : drivers)
            buffer.append(driver.getName()).append('\t')
                    .append(driver.getUrl()).append('\n');
        buffer.append("Total:").append(' ').append(drivers.length).append(' ')
                .append("drivers").append('\n');
        buffer.append('\n');
        WorkloadInfo[] workloads = controller.getActiveWorkloads();
        buffer.append("Active Workloads:").append('\n');
        for (WorkloadInfo workload : workloads) {
            buffer.append(workload.getId()).append('\t')
                    .append(workload.getSubmitDate()).append('\t')
                    .append(workload.getState()).append('\t');
                    if (workload.getCurrentStage() != null) {
                    	buffer.append(workload.getCurrentStage().getId()).append('\n');	
                    } else {
                    	buffer.append("None").append('\n');
                    }
        }
        buffer.append("Total:").append(' ').append(workloads.length)
                .append(' ').append("active workloads").append('\n');
        return buffer.toString();
    }

}
