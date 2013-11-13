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

import static com.intel.cosbench.model.MissionState.*;

import java.util.Arrays;

import com.intel.cosbench.bench.*;
import com.intel.cosbench.model.MissionInfo;
import com.intel.cosbench.protocol.*;

public class QueryHandler extends MissionHandler {

    @Override
    protected Response process(MissionInfo info) {
//        if (isStopped(info.getState()))
//            return new Response(false, "mission aleady stopped");
        return getResponse(info);
    }

    private Response getResponse(MissionInfo info) {
        QueryResponse response = new QueryResponse();
        Snapshot snapshot = info.getSnapshot();
        response.setTime(snapshot.getTimestamp());
        response.setVersion(snapshot.getVersion());
        response.setMinVersion(snapshot.getMinVersion());
        response.setMaxVersion(snapshot.getMaxVersion());
        Report report = snapshot.getReport();
        response.setRunning(info.getState().equals(LAUNCHED));
        response.setReport(Arrays.asList(report.getAllMetrics()));
        return response;
    }

}
