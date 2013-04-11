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

import org.apache.commons.lang.StringUtils;

import com.intel.cosbench.model.WorkloadInfo;
import com.intel.cosbench.service.ControllerService;
import com.intel.cosbench.web.*;

public class WorkloadHandler extends AbstractClientHandler {

    protected ControllerService controller;

    public void setController(ControllerService controller) {
        this.controller = controller;
    }

    @Override
    protected String process(HttpServletRequest req, HttpServletResponse res)
            throws Exception {
        String id = req.getParameter("id");
        if (StringUtils.isEmpty(id))
            throw new BadRequestException();
        return process(id);
    }

    protected String process(String id) {
        WorkloadInfo info = controller.getWorkloadInfo(id);
        if (info == null)
            throw new NotFoundException();
        return process(info);
    }

    protected String process(WorkloadInfo info) {
        StringBuilder buffer = new StringBuilder();
        buffer.append(info.getId()).append('\t').append(info.getSubmitDate())
                .append('\t').append(info.getState()).append('\n');
        return buffer.toString();
    }

}
