/**

Copyright 2013 Intel Corporation, All Rights Reserved.
Copyright 2019 OpenIO Corporation, All Rights Reserved.

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
package com.intel.cosbench.controller.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.intel.cosbench.service.ControllerService;
import com.intel.cosbench.utils.AuthValidator;
import com.intel.cosbench.web.AbstractController;

public class LogonPageController extends AbstractController {


    @SuppressWarnings("unused")
    private ControllerService controller;

    public void setController(ControllerService controller) {
        this.controller = controller;
    }

    @Override
    protected ModelAndView process(HttpServletRequest req,
            HttpServletResponse res) {
        ModelAndView result = new ModelAndView("logon");
        try {
            if(AuthValidator.NeedLogon()) {
                result.addObject("username", AuthValidator.USERNAME);
                result.addObject("password", AuthValidator.PASSWD);
                result.addObject("hidden","visibility:hidden");
            }
            else{
                result.addObject("username", "");
                result.addObject("password", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        result.addObject("cInfo", controller.getControllerInfo());
        return result;
    }
}
