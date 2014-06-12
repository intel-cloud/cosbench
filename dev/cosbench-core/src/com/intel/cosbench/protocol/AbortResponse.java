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

package com.intel.cosbench.protocol;

import java.util.List;

import com.intel.cosbench.bench.Metrics;


/**
 * The response to get log from driver when aborted.
 * 
 * @author ywang19, qzheng7
 *
 */
public class AbortResponse extends Response {

    private String driverLog; /* driver log */
    private List<Metrics> report; /* metrics report */

    public AbortResponse() {
        /* empty */
    }

    public String getDriverLog() {
        return driverLog;
    }

    public void setDriverLog(String driverLog) {
        this.driverLog = driverLog;
    }
    
    public List<Metrics> getReport() {
        return report;
    }

    public void setReport(List<Metrics> report) {
        this.report = report;
    }

}
