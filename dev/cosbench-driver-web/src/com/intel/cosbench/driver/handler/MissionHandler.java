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

import java.util.Scanner;

import javax.servlet.http.*;

import com.intel.cosbench.log.LogFactory;
import com.intel.cosbench.log.Logger;
import com.intel.cosbench.model.MissionInfo;
import com.intel.cosbench.protocol.Response;
import com.intel.cosbench.service.DriverService;
import com.intel.cosbench.web.*;

abstract class MissionHandler extends AbstractCommandHandler {

    protected DriverService driver;
    protected static final Logger LOGGER = LogFactory.getSystemLogger();

    public void setDriver(DriverService driver) {
        this.driver = driver;
    }

    protected abstract Response process(MissionInfo info);

    @Override
    protected Response process(HttpServletRequest req, HttpServletResponse res)
            throws Exception {
        Scanner scanner = new Scanner(req.getInputStream());
        MissionInfo info = getMissionInfo(scanner);
        return process(info);
    }

    private MissionInfo getMissionInfo(Scanner scanner) {
        if (!scanner.hasNext())
            throw new BadRequestException();
        MissionInfo info = driver.getMissionInfo(scanner.next());
        if (info == null)
            throw new NotFoundException();
        return info;
    }

}
