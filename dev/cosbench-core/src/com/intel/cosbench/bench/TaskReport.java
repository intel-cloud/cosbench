/**

Copyright 2013 Intel Corporation, All Rights Reserved.
Copyright 2019 OpenIO Corporation, All Rights Reserved.

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
package com.intel.cosbench.bench;
/**
 * The class is the data structure of task info
 *
 * @author liyuan
 *
 */
public class TaskReport {
    private String driverName;
    private String driverUrl;
    private Report report;
    public String getDriverName() {
        return driverName;
    }
    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }
    public String getDriverUrl() {
        return driverUrl;
    }
    public void setDriverUrl(String driverUrl) {
        this.driverUrl = driverUrl;
    }
    public Report getReport() {
        return report;
    }
    public void setReport(Report report) {
        this.report = report;
    }

}
