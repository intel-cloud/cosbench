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

package com.intel.cosbench.exporter;

import java.io.*;

import com.intel.cosbench.model.*;

class SimpleLogExporter implements LogExporter {

    private WorkloadInfo workload;

    public SimpleLogExporter() {
        /* empty */
    }

    public void setWorkload(WorkloadInfo workload) {
        this.workload = workload;
    }

    @Override
    public void export(Writer writer) throws IOException {
        for (StageInfo stage : workload.getStageInfos())
            exportStageLog(writer, stage);
        writer.flush();
    }

    private void exportStageLog(Writer writer, StageInfo stage)
            throws IOException {
        writer.write("=========================");
        writer.write("=========================");
        writer.write(" stage: " + stage.getId() + ' ');
        writer.write("=========================");
        writer.write("=========================");
        writer.write('\n');
        for (TaskInfo task : stage.getTaskInfos())
            exportTaskLog(writer, task);
        writer.flush();
    }

    private void exportTaskLog(Writer writer, TaskInfo task) throws IOException {
        writer.write("-----------------");
        writer.write("-----------------");
        writer.write(getTaskLogHeader(task));
        writer.write("-----------------");
        writer.write("-----------------");
        writer.write('\n');
        writer.write(task.getLog());
    }

    private String getTaskLogHeader(TaskInfo task) {
        String header = " mission: " + task.getMissionId();
        String driver = task.getSchedule().getDriver().getName();
        header += ", driver: " + driver + ' ';
        return header;
    }

}
