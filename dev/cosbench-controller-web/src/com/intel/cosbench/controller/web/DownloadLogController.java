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

import java.io.*;
import java.util.Map;

import javax.servlet.http.*;

import org.apache.commons.io.IOUtils;
import org.springframework.web.servlet.*;

import com.intel.cosbench.model.WorkloadInfo;

public class DownloadLogController extends WorkloadPageController {

    private static final View LOG = new LogView();

    private static class LogView implements View {

        @Override
        public String getContentType() {
            return "text/plain";
        }

        @Override
        public void render(Map<String, ?> model, HttpServletRequest req,
                HttpServletResponse res) throws Exception {
            File log = (File) model.get("log");
            res.setHeader("Content-Length", String.valueOf(log.length()));
            res.setHeader("Content-Disposition",
                    "attachment; filename=\"workload-log.txt\"");
            FileInputStream input = new FileInputStream(log);
            try {
                IOUtils.copyLarge(input, res.getOutputStream());
            } finally {
                IOUtils.closeQuietly(input);
            }
        }

    }

    protected ModelAndView process(WorkloadInfo info) {
        File file = controller.getWorkloadLog(info);
    	ModelAndView result = new ModelAndView(LOG, "log", file);
    	result.addObject("cInfo", controller.getControllerInfo());
        return result;
    }

}
