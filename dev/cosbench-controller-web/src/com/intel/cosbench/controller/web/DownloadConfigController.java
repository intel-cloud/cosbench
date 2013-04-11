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

public class DownloadConfigController extends WorkloadPageController {

    private static final View CONFIG = new ConfigView();

    private static class ConfigView implements View {

        @Override
        public String getContentType() {
            return "application/xml";
        }

        @Override
        public void render(Map<String, ?> model, HttpServletRequest req,
                HttpServletResponse res) throws Exception {
            File config = (File) model.get("config");
            res.setHeader("Content-Length", String.valueOf(config.length()));
            res.setHeader("Content-Disposition",
                    "attachment; filename=\"workload-config.xml\"");
            FileInputStream input = new FileInputStream(config);
            try {
                IOUtils.copyLarge(input, res.getOutputStream());
            } finally {
                IOUtils.closeQuietly(input);
            }
        }

    }

    protected ModelAndView process(WorkloadInfo info) {
        File file = controller.getWorkloadConfig(info);
        return new ModelAndView(CONFIG, "config", file);
    }

}
