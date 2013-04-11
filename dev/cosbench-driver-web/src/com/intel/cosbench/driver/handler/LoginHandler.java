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

import static com.intel.cosbench.model.MissionState.AUTHED;

import com.intel.cosbench.model.MissionInfo;
import com.intel.cosbench.protocol.*;

class LoginHandler extends MissionHandler {

    @Override
    protected Response process(MissionInfo info) {
        String id = info.getId();
        driver.login(id);
        if (!info.getState().equals(AUTHED))
            return new Response(false, "fail to login - see log for details");
        return new LoginResponse();
    }

}
