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

import static com.intel.cosbench.model.TaskState.BOOTED;

import org.apache.commons.lang.StringUtils;

import com.intel.cosbench.controller.model.TaskContext;
import com.intel.cosbench.model.DriverInfo;
import com.intel.cosbench.protocol.PingResponse;

/**
 * The class encapsulates how to handle boot request/response, internally, it
 * issues command to ping destination.
 * 
 * @author ywang19, qzheng7
 * 
 */
class Bootor extends AbstractCommandTasklet<PingResponse> {

    public Bootor(TaskContext context) {
        super(context, PingResponse.class);
    }

    @Override
    public void execute() {
        initHttpClient();
        initObjectMapper();
        issueCommand("ping");
        context.setState(BOOTED);
    }

    @Override
    protected void handleResponse(PingResponse response) {
    	long driverTime = 0;
    	DriverInfo driver = getDriver();
    	try {
			driverTime = Long.parseLong(response.getTimeStamp());
		} catch (NumberFormatException e) {
			LOGGER.debug("time stamp of driver {} can not be formated", driver.getName());
		}
        timeDrift = System.currentTimeMillis() - driverTime;
    	if (!StringUtils.equals(response.getName(), driver.getName())){
    		String msg = "expetect driver name {} dose not match the real name {}";
    		LOGGER.debug(msg, driver.getName(), response.getName());
    	}
    }

}
