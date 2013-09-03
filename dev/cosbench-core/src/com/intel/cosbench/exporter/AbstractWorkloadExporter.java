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
import com.intel.cosbench.model.StageInfo;
import com.intel.cosbench.model.WorkloadInfo;

/**
 * This class is the base class for exporting workload information.
 * 
 * @author ywang19, qzheng7
 *
 */
abstract class AbstractWorkloadExporter implements WorkloadExporter {

    protected WorkloadInfo workload;

    public AbstractWorkloadExporter() {
        /* empty */
    }

    public void setWorkload(WorkloadInfo workload) {
        this.workload = workload;
    }

    @Override
    public void export(Writer writer) throws IOException {
        writeHeader(writer);
        writer.flush();
        for (StageInfo stage : workload.getStageInfos()) {
        	for (Metrics metrics : stage.getReport())
                writeMetrics(writer, metrics, stage);
			if (stage.getReport().getSize() == 0)
				writeMetrics(writer, stage);
        }
        writer.flush();
    }

    protected abstract void writeMetrics(Writer writer, StageInfo stage) throws IOException;

	protected abstract void writeHeader(Writer writer) throws IOException;

    protected abstract void writeMetrics(Writer writer, Metrics metrics, StageInfo stage)
            throws IOException;

}
