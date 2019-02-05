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

import com.intel.cosbench.bench.Metrics;
import com.intel.cosbench.bench.Report;
import com.intel.cosbench.bench.TaskReport;
import com.intel.cosbench.model.DriverInfo;
import com.intel.cosbench.model.StageInfo;
import com.intel.cosbench.model.TaskInfo;
import com.intel.cosbench.model.WorkloadInfo;

public abstract class AbstractWorkerExporter implements WorkerExporter {

    private StageInfo stageInfo;

    public StageInfo getStageInfo() {
        return stageInfo;
    }

    public void setStageInfo(StageInfo stageInfo) {
        this.stageInfo = stageInfo;
    }

    @Override
    public void export(Writer writer) throws IOException {
        writeReport(writer);
    }

    private void writeReport(Writer writer) throws IOException {
        writeHeader(writer);
        writer.flush();
        for(TaskInfo taskInfo : stageInfo.getTaskInfos()){
            for(Metrics metrics: taskInfo.getWrReport()){
                writeMetrics(writer,metrics);
            }
        }
        writer.flush();
    }

    protected abstract void writeHeader(Writer writer) throws IOException;

    protected abstract void writeMetrics(Writer writer, Metrics metrics)
            throws IOException;

}
