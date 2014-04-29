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

package com.intel.cosbench.driver.handler;

import java.util.Map;

import javax.servlet.http.*;

import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.mvc.Controller;

import com.intel.cosbench.config.ConfigException;
import com.intel.cosbench.log.*;
import com.intel.cosbench.protocol.Response;
import com.intel.cosbench.service.IllegalStateException;
import com.intel.cosbench.web.*;

abstract class AbstractCommandHandler implements Controller {

    protected static final Logger LOGGER = LogFactory.getSystemLogger();

    private static final View JSON = new JsonView();

    private static class JsonView implements View {

        private ObjectMapper mapper;

        public JsonView() {
            mapper = new ObjectMapper();
            SerializationConfig config = mapper.copySerializationConfig();
            config.setSerializationInclusion(Inclusion.NON_NULL);
            mapper.setSerializationConfig(config);
        }

        @Override
        public String getContentType() {
            return "application/json";
        }

        @Override
        public void render(Map<String, ?> model, HttpServletRequest req,
                HttpServletResponse res) throws Exception {
            Response response = (Response) model.get("response");
            res.setStatus(response.getCode());
            res.setContentType("application/json");
            mapper.writeValue(res.getWriter(), response);
        }

    }

    protected abstract Response process(HttpServletRequest req,
            HttpServletResponse res) throws Exception;

    @Override
    public ModelAndView handleRequest(HttpServletRequest req,
            HttpServletResponse res) {
        Response response = null;
        try {
            response = process(req, res);
        } catch (BadRequestException bre) {
            response = new Response(400, "unrecognized request: " + req.toString());
        } catch (NotFoundException nfe) {
            response = new Response(404, "mission not found");
        } catch (IllegalStateException ise) {
            response = new Response(409, ise.getMessage());
        } catch (ConfigException ce) {
            response = new Response(400, ce.getMessage());
        } catch (Exception e) {
            response = new Response(500, e.getMessage());
            LOGGER.error("unexpected error", e);
        }
        return new ModelAndView(JSON, "response", response);
    }

}
