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

import com.intel.cosbench.bench.Metrics;
import com.intel.cosbench.model.*;

/**
 * This class is the base class for exporting performance matrix.
 * 
 * @author ywang19, qzheng7
 *
 */
abstract class AbstractMatrixExporter implements MatrixExporter {

    private int idx;
    protected WorkloadInfo workload;

    public AbstractMatrixExporter() {
        /* empty */
    }

    public void setWorkload(WorkloadInfo workload) {
        this.workload = workload;
    }

    @Override
    public void init(Writer writer) throws IOException {
        writeHeader(writer);
        writer.flush();
    }

    protected abstract void writeHeader(Writer writer) throws IOException;

    @Override
    public void export(Writer writer) throws IOException {
        for (StageInfo stage : workload.getStageInfos()) {
            idx = 1;
            for (Metrics metrics : stage.getReport())
                writeMetrics(writer, stage, metrics, idx++);
        }
        writer.flush();
    }

    protected abstract void writeMetrics(Writer writer, StageInfo stage,
            Metrics metrics, int idx) throws IOException;

}
