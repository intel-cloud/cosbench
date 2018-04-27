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

import static com.intel.cosbench.model.StageState.*;

import java.io.IOException;

import javax.servlet.http.*;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import com.intel.cosbench.model.*;
import com.intel.cosbench.service.ControllerService;
import com.intel.cosbench.web.*;

public class StagePageController extends AbstractController {

    protected ControllerService controller;

    public void setController(ControllerService controller) {
        this.controller = controller;
    }

    @Override
    protected ModelAndView process(HttpServletRequest req,
            HttpServletResponse res) {
        String wid = req.getParameter("wid");
        if (StringUtils.isEmpty(wid))
            throw new BadRequestException();
        String sid = req.getParameter("sid");
        if (StringUtils.isEmpty(sid))
            throw new BadRequestException();
        return process(wid, sid);
    }

    protected ModelAndView process(String wid, String sid) {
        WorkloadInfo wInfo = controller.getWorkloadInfo(wid);
        if (wInfo == null)
            throw new NotFoundException();
        StageInfo sInfo = wInfo.getStageInfo(sid);
        if (sInfo == null)
            throw new NotFoundException();
		if (controller.getloadArch() && sInfo.getSnapshotRegistry().getSize() == 0)
			try {
				controller.getWorkloadLoader().loadStagePageInfo(wInfo,
						sInfo.getId());
			} catch (IOException e) {
				e.printStackTrace();
			}
        return process(wInfo, sInfo);
    }

    protected ModelAndView process(WorkloadInfo wInfo, StageInfo sInfo) {
        ModelAndView result = new ModelAndView("stage");
        result.addObject("cInfo", controller.getControllerInfo());
        result.addObject("sInfo", sInfo);
        result.addObject("wInfo", wInfo);
        result.addObject("isStopped", isStopped(sInfo.getState()));
        result.addObject("isRunning", isRunning(sInfo.getState()));
        return result;
    }

}
