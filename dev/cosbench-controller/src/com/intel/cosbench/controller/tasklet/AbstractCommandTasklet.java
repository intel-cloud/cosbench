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

package com.intel.cosbench.controller.tasklet;

import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.DeserializationConfig.Feature;

import com.intel.cosbench.controller.model.TaskContext;
import com.intel.cosbench.protocol.Response;

/**
 * The base class encapsulates commands through HTTP request.
 * 
 * @author ywang19, qzheng7
 */
abstract class AbstractCommandTasklet<T extends Response> extends
        AbstractHttpTasklet {

    private Class<T> clazz;

    protected abstract void handleResponse(T response);

    public AbstractCommandTasklet(TaskContext context, Class<T> clazz) {
        super(context);
        this.clazz = clazz;
    }

    protected void initObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        DeserializationConfig config = mapper.copyDeserializationConfig();
        config.disable(Feature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setDeserializationConfig(config);
        context.setMapper(mapper);
    }

    protected void issueCommand(String command) {
        issueCommand(command, null);
    }

    protected void issueCommand(String command, String content) {
        T response = null;
        String body = issueHttpRequest(command, content);
        try {
            response = context.getMapper().readValue(body, clazz);
        } catch (Exception e) {
            LOGGER.error("cannot parse response body", e);
            throw new TaskletException(); // mark termination
        }
        if (!response.isSucc()) {
            String msg = "driver report error: HTTP {} - {}";
            LOGGER.error(msg, response.getCode(), response.getError());
            throw new TaskletException(); // mark termination
        }
        handleResponse(response); // specific response handling
    }

}
