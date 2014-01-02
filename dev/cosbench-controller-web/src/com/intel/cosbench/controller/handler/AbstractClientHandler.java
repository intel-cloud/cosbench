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

import java.io.*;
import java.util.Map;

import javax.servlet.http.*;

import org.springframework.web.servlet.*;
import org.springframework.web.servlet.mvc.Controller;

import com.intel.cosbench.config.ConfigException;
import com.intel.cosbench.service.IllegalStateException;
import com.intel.cosbench.web.*;

abstract class AbstractClientHandler implements Controller {

    private static View TEXT = new TextView();

    private static class TextView implements View {

        @Override
        public String getContentType() {
            return "text/plain";
        }

        @Override
        public void render(Map<String, ?> model, HttpServletRequest req,
                HttpServletResponse res) throws Exception {
            PrintWriter writer = res.getWriter();
            writer.print(model.get("message"));
        }

    }

    protected abstract String process(HttpServletRequest req,
            HttpServletResponse res) throws Exception;

    @Override
    public ModelAndView handleRequest(HttpServletRequest req,
            HttpServletResponse res) throws Exception {
        String message = null;
        try {
            message = process(req, res);
        } catch (BadRequestException bre) {
            message = "Bad Request";
        } catch (NotFoundException nfe) {
            message = "Not Found";
        } catch (IllegalStateException ise) {
            message = ise.getMessage();
        } catch (ConfigException ce) {
            message = ce.getMessage();
        } catch(FileNotFoundException fnfe) {
        	message = fnfe.getMessage();
        } catch(IOException ie) {
        	message = ie.getMessage();
        } catch (Exception e) {
            StringWriter writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            message = "Internal Error\n" + writer.toString();
        }
        return new ModelAndView(TEXT, "message", message);
    }

}
