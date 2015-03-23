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

import java.io.InputStream;
import java.util.List;

import javax.servlet.http.*;

import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.web.servlet.ModelAndView;

import com.intel.cosbench.config.*;
import com.intel.cosbench.service.DriverService;
import com.intel.cosbench.web.*;

public class MissionSubmissionController extends AbstractController {

    private DriverService driver;

    public void setDriver(DriverService driver) {
        this.driver = driver;
    }

    @Override
    protected ModelAndView process(HttpServletRequest req,
            HttpServletResponse res) throws Exception {
        String id = null;
        try {
            InputStream stream = retrieveConfigStream(req);
            id = driver.submit(new XmlConfig(stream));
        } catch (ConfigException ce) {
            return createErrResult(ce.getMessage());
        }
        return createSuccResult(id);
    }

    @SuppressWarnings("unchecked")
    private InputStream retrieveConfigStream(HttpServletRequest request)
            throws Exception {
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        for (FileItem item : (List<FileItem>) upload.parseRequest(request))
            if (item.getFieldName().equals("config"))
                return item.getInputStream();
        throw new BadRequestException();
    }

    private ModelAndView createErrResult(String msg) {
        ModelAndView result = new ModelAndView("submit");
        result.addObject("dInfo", driver.getDriverInfo());
        result.addObject("aInfos", driver.getActiveMissions());
        result.addObject("error", msg);
        return result;
    }

    private ModelAndView createSuccResult(String id) {
        ModelAndView result = new ModelAndView("submit");
        result.addObject("dInfo", driver.getDriverInfo());
        result.addObject("aInfos", driver.getActiveMissions());
        result.addObject("submitted", "your mission has been accepted");
        result.addObject("id", id);
        return result;
    }

}
