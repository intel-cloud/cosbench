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

import com.intel.cosbench.bench.*;
import com.intel.cosbench.model.WorkloadInfo;

/**
 * This class is the base class for exporting response time histogram.
 * 
 * @author ywang19, qzheng7
 *
 */
abstract class AbstractLatencyExporter implements LatencyExporter {

    protected int[] accs;
    protected int[] sums;

    protected WorkloadInfo workload;

    public AbstractLatencyExporter() {
        /* empty */
    }

    public void setWorkload(WorkloadInfo workload) {
        this.workload = workload;
    }

    @Override
    public void export(Writer writer) throws IOException {
        writeHeader(writer);
        writer.flush();
        int size = 1 + Counter.UL;
        Report report = workload.getReport();
        int metricsIdx = 0;
        int metricsNum = report.getSize();
        sums = new int[metricsNum];
        accs = new int[metricsNum];
        for (Metrics metrics : workload.getReport()) {
            int sum = 0;
            if(metrics.getLatency() == null)
            	continue;
            int[] data = metrics.getLatency().getHistoData();
            for (int idx = 0; idx < size; idx++)
                sum += data[idx];
            sums[metricsIdx++] = sum;
        }
        for (int idx = 0; idx < size; idx++)
            writeHistogram(writer, idx);
        writer.flush();
    }

    protected abstract void writeHeader(Writer writer) throws IOException;

    protected abstract void writeHistogram(Writer writer, int idx)
            throws IOException;

}
