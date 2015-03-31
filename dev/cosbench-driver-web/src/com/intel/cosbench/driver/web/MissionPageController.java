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

package com.intel.cosbench.driver.web;

import static com.intel.cosbench.model.MissionState.*;

import javax.servlet.http.*;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import com.intel.cosbench.model.MissionInfo;
import com.intel.cosbench.service.DriverService;
import com.intel.cosbench.web.*;

public class MissionPageController extends AbstractController {

    protected DriverService driver;

    public void setDriver(DriverService driver) {
        this.driver = driver;
    }

    @Override
    protected ModelAndView process(HttpServletRequest req,
            HttpServletResponse res) throws Exception {
        String id = req.getParameter("id");
        if (StringUtils.isEmpty(id))
            throw new BadRequestException();
        return process(id);
    }

    protected ModelAndView process(String id) {
        MissionInfo info = driver.getMissionInfo(id);
        if (info == null)
            throw new NotFoundException();
        return process(info);
    }

    protected ModelAndView process(MissionInfo info) {
        ModelAndView result = new ModelAndView("mission");
        result.addObject("info", info);
        result.addObject("dInfo", driver.getDriverInfo());
        result.addObject("isStopped", isStopped(info.getState()));
        result.addObject("isRunning", isRunning(info.getState()));
        result.addObject("toBeAuthed", allowAuth(info.getState()));
        result.addObject("toBeLaunched", allowLaunch(info.getState()));
        result.addObject("toBeClosed", allowClose(info.getState()));
        return result;
    }

}
