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

import java.util.*;

import com.intel.cosbench.bench.Metrics;


/**
 * The response for request to query snapshot from driver.
 * 
 * @author ywang19, qzheng7
 *
 */
public class QueryResponse extends Response {

    private Date time; /* snapshot time stamp */

    private int version; /* snapshot version number */
    private int minVersion; /* maximum snapshot version */
    private int maxVersion; /* minimum snapshot version */

    private boolean running; /* is mission running */
    private List<Metrics> report; /* metrics report */

    public QueryResponse() {
        /* empty */
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getMinVersion() {
        return minVersion;
    }

    public void setMinVersion(int minVersion) {
        this.minVersion = minVersion;
    }

    public int getMaxVersion() {
        return maxVersion;
    }

    public void setMaxVersion(int maxVersion) {
        this.maxVersion = maxVersion;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public List<Metrics> getReport() {
        return report;
    }

    public void setReport(List<Metrics> report) {
        this.report = report != null? report: new ArrayList<Metrics>();
    }

}
