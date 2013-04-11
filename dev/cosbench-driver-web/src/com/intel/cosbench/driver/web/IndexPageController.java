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

import javax.servlet.http.*;

import org.springframework.web.servlet.ModelAndView;

import com.intel.cosbench.service.DriverService;
import com.intel.cosbench.web.AbstractController;

/**
 * The home page of driver web console.
 * 
 * @author ywang19, qzheng7
 *
 */
public class IndexPageController extends AbstractController {

    private DriverService driver;

    public void setDriver(DriverService driver) {
        this.driver = driver;
    }

    @Override
    protected ModelAndView process(HttpServletRequest req,
            HttpServletResponse res) throws Exception {
        ModelAndView result = new ModelAndView("index");
        result.addObject("dInfo", driver.getDriverInfo());
        result.addObject("aInfos", driver.getActiveMissions());
        result.addObject("hInfos", driver.getHistoryMissions());
        return result;
    }

}
