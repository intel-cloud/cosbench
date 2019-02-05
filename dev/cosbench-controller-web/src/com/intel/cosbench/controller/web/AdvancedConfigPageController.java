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

package com.intel.cosbench.controller.web;

import javax.servlet.http.*;

import org.springframework.web.servlet.ModelAndView;

import com.intel.cosbench.service.ControllerService;
import com.intel.cosbench.web.AbstractController;

public class AdvancedConfigPageController extends AbstractController {

    @SuppressWarnings("unused")
    private ControllerService controller;

    public void setController(ControllerService controller) {
        this.controller = controller;
    }

    @Override
    protected ModelAndView process(HttpServletRequest req,
            HttpServletResponse res) {
        ModelAndView result = new ModelAndView("advanced-config");
        result.addObject("cInfo", controller.getControllerInfo());
        return result;
    }

}
