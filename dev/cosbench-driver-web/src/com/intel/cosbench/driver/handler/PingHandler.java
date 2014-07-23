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

import java.io.IOException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Scanner;

import javax.servlet.http.*;

import com.intel.cosbench.model.DriverInfo;
import com.intel.cosbench.protocol.*;
import com.intel.cosbench.service.DriverService;
import com.intel.cosbench.web.BadRequestException;

public class PingHandler extends AbstractCommandHandler {

    private DriverService driver;

    public void setDriver(DriverService driver) {
        this.driver = driver;
    }

    @Override
    protected Response process(HttpServletRequest req, HttpServletResponse res)
    		throws Exception {
    	Scanner scanner = new Scanner(req.getInputStream());
    	setSysTime(getControllerTime(scanner));
    	
    	PingResponse response = new PingResponse();
        DriverInfo info = driver.getDriverInfo();
        response.setName(info.getName());
        response.setAddress(info.getUrl());
        response.setTimeStamp(String.valueOf(System.currentTimeMillis()));
        return response;
    }
    
    private long getControllerTime(Scanner scanner) throws NumberFormatException {
    	if (!scanner.hasNext())
            throw new BadRequestException();
    	return Long.parseLong(scanner.next());
	}

    private void setSysTime(long ctrTime) throws IOException {
    	DateFormat dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	String[] cmd = {"date", "-s", dateTime.format(new Date(ctrTime))};
    	String osType = System.getProperty("os.name").toLowerCase();
    	if (osType.contains("linux")) {
    		LOGGER.debug("setting system time {} on driver {}", ctrTime, driver.getDriverInfo().getName());
    		Runtime.getRuntime().exec(cmd);
		} else {
			LOGGER.warn("os type on driver {} is {}!", 
					driver.getDriverInfo().getName(), osType);
		}
	}

}
