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

import java.io.IOException;
import java.util.Arrays;

import com.intel.cosbench.bench.Report;
import com.intel.cosbench.model.MissionInfo;
import com.intel.cosbench.protocol.*;

public class AbortHandler extends MissionHandler {

    @Override
    protected Response process(MissionInfo info) {
        String id = info.getId();
        driver.abort(id);
        return getResponse(info);
    }

    private Response getResponse(MissionInfo info) {
        AbortResponse response = new AbortResponse();
        Report report = info.getReport();
        response.setReport(Arrays.asList(report.getAllMetrics()));
        String log = null;
        try {
            log = info.getLogManager().getLogAsString();
        } catch (IOException e) {
            log = "[N/A]";
        }
        response.setDriverLog(log);
        return response;
    }

}
