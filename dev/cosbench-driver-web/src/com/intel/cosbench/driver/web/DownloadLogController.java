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
package com.intel.cosbench.driver.web;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import com.intel.cosbench.model.MissionInfo;

public class DownloadLogController extends MissionPageController{
    private static final View LOG = new LogView();

    private static class LogView implements View {

        @Override
        public String getContentType() {
            return "text/plain";
        }

        @Override
        public void render(Map<String, ?> model, HttpServletRequest req,
                HttpServletResponse res) throws Exception {
            String log = (String) model.get("log");
            res.setHeader("Content-Length", String.valueOf(log.length()));
            res.setHeader("Content-Disposition",
                    "attachment; filename=\"mission-log.txt\"");
           res.getOutputStream().write(log.getBytes());
        }

    }

    protected ModelAndView process(MissionInfo info) {
        String log = "";
        try {
            log = info.getLogManager().getLogAsString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ModelAndView(LOG, "log", log);
    }

}
