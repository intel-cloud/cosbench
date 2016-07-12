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

package com.intel.cosbench.web;

import java.io.*;

import javax.servlet.http.*;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;


/**
 * The base class encapsulates spring mvc controller.
 * 
 * @author ywang19, qzheng7
 *
 */
public abstract class AbstractController implements Controller {

    protected abstract ModelAndView process(HttpServletRequest req,
            HttpServletResponse res) throws Exception;

    @Override
    public ModelAndView handleRequest(HttpServletRequest req,
            HttpServletResponse res) throws Exception {
        ModelAndView result;
        try {
            result = process(req, res);
        } catch (BadRequestException bre) {
            return new ModelAndView("400");
        } catch (NotFoundException nfe) {
            return new ModelAndView("404");
        } catch (FileNotFoundException fnfe) {
        	return new ModelAndView("404", "resource", fnfe.getMessage());
        } catch (Exception e) {
            StringWriter writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            return new ModelAndView("500", "cause", writer.toString());
        }
        String showDetails = req.getParameter("showDetails");
        result.addObject("showDetails", Boolean.parseBoolean(showDetails));
        String perfDetails = req.getParameter("perfDetails");
        result.addObject("perfDetails", Boolean.parseBoolean(perfDetails));
        String showErrorStatistics = req.getParameter("showErrorStatistics");
        result.addObject("showErrorStatistics",Boolean.parseBoolean(showErrorStatistics));
        return result;
    }

}
