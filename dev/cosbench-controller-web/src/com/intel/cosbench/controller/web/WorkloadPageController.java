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

import static com.intel.cosbench.model.WorkloadState.*;

import java.io.IOException;

import javax.servlet.http.*;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import com.intel.cosbench.model.*;
import com.intel.cosbench.service.ControllerService;
import com.intel.cosbench.web.*;

public class WorkloadPageController extends AbstractController {

    protected ControllerService controller;

    public void setController(ControllerService controller) {
        this.controller = controller;
    }

    @Override
    protected ModelAndView process(HttpServletRequest req,
            HttpServletResponse res) {
        String id = req.getParameter("id");
        if (StringUtils.isEmpty(id))
            throw new BadRequestException();
        return process(id);
    }

    protected ModelAndView process(String id) {
        WorkloadInfo info = controller.getWorkloadInfo(id);
        if (info == null)
            throw new NotFoundException();
		if (controller.getloadArch() && info.getArchived() && info.getReport().getAllMetrics().length==0) {
			try {
				controller.getWorkloadLoader().loadWorkloadPageInfo(info);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
        return process(info);
    }

    protected ModelAndView process(WorkloadInfo info) {
        ModelAndView result = new ModelAndView("workload");
        StageInfo stage = info.getCurrentStage();
        result.addObject("cInfo", controller.getControllerInfo());
        result.addObject("info", info);
        result.addObject("isStopped", isStopped(info.getState()));
        result.addObject("isRunning", isRunning(info.getState()));
        result.addObject("isStageRunning",
                stage == null ? false : StageState.isRunning(stage.getState()));
        return result;
    }

}
