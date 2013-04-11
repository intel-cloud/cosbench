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

import static com.intel.cosbench.model.MissionState.TERMINATED;

import com.intel.cosbench.model.MissionInfo;
import com.intel.cosbench.protocol.*;

public class LaunchHandler extends MissionHandler {

    @Override
    protected Response process(MissionInfo info) {
        String id = info.getId();
        driver.launch(id);
        if (info.getState().equals(TERMINATED))
            return new Response(false, "launch failed");
        return getResponse(info);
    }

    private Response getResponse(MissionInfo info) {
        LaunchResponse response = new LaunchResponse();
        response.setInterval(info.getMission().getInterval());
        return response;
    }

}
