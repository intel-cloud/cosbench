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
package com.intel.cosbench.exporter;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import com.intel.cosbench.bench.Snapshot;
import com.intel.cosbench.bench.TaskReport;
import com.intel.cosbench.model.DriverInfo;
import com.intel.cosbench.model.StageInfo;
import com.intel.cosbench.model.WorkloadInfo;

public abstract class AbstractTaskExporter implements TaskExporter {

    protected WorkloadInfo workloadInfo;
    protected DriverInfo driverInfo;
    protected List<TaskReport> driverTasks = new ArrayList<TaskReport>();

    public WorkloadInfo getWorkloadInfo() {
        return workloadInfo;
    }

    public void setWorkloadInfo(WorkloadInfo workloadInfo) {
        this.workloadInfo = workloadInfo;
    }

    public DriverInfo getDriverInfo() {
        return driverInfo;
    }

    public void setDriverInfo(DriverInfo driverInfo) {
        this.driverInfo = driverInfo;
    }


     @Override
    public void export(Writer writer) throws IOException {
         //check every stage every task
        for(StageInfo sInfo:workloadInfo.getStageInfos()){
            for(TaskReport tReport:sInfo.getTaskReports()){
                if(tReport.getDriverUrl().equals(driverInfo.getUrl())){
                    driverTasks.add(tReport);
                }
            }
        }
        writeReport(writer);
    }

    private void writeReport(Writer writer) throws IOException {
        writeHeader(writer);
        writer.flush();
        for (TaskReport tReport : driverTasks)
            writeMetrics(writer, tReport);
        writer.flush();
    }

    protected abstract void writeHeader(Writer writer) throws IOException;

    protected abstract void writeMetrics(Writer writer, TaskReport tReport)
            throws IOException;
}
