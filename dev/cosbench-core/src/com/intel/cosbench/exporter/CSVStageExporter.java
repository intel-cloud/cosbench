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

import static com.intel.cosbench.exporter.Formats.*;

import java.io.*;
import java.util.Arrays;

import com.intel.cosbench.bench.*;

/**
 * This class is to export stage information into CSV format.
 * 
 * @author ywang19, qzheng7
 *
 */
class CSVStageExporter extends AbstractStageExporter {

    public CSVStageExporter() {
        /* empty */
    }

    protected void writeHeader(Writer writer) throws IOException {
        StringBuilder buffer = new StringBuilder();
        buffer.append("Timestamp").append(',');
        char[] cs = new char[numOpTypes];
        Arrays.fill(cs, ',');
        String suffix = new String(cs);
        buffer.append("Op-Count").append(suffix);
        buffer.append("Byte-Count").append(suffix);
        buffer.append("Avg-ResTime").append(suffix);
        buffer.append("Throughput").append(suffix);
        buffer.append("Bandwidth").append(suffix);
        buffer.append("Succ-Ratio").append(suffix);
        buffer.append("Version-Info");
        buffer.append(',').append(',').append('\n').append(',');
        for (int i = 0; i < 6; i++)
            // 6 metrics
            for (Metrics metrics : snapshots[0].getReport())
                buffer.append(metrics.getSampleType()).append(',');
        buffer.append("Min-Version").append(',');
        buffer.append("Version").append(',');
        buffer.append("Max-Version").append('\n');
        writer.write(buffer.toString());
    }

    protected void writeMetrics(Writer writer, Snapshot snapshot)
            throws IOException {
        StringBuilder buffer = new StringBuilder();
        buffer.append(TIME.format(snapshot.getTimestamp())).append(',');
        /* Operation Count */
        for (Metrics metrics : snapshot.getReport())
            buffer.append(metrics.getSampleCount()).append(',');
        /* Byte Count */
        for (Metrics metrics : snapshot.getReport())
            buffer.append(metrics.getByteCount()).append(',');
        /* Response Time */
        for (Metrics metrics : snapshot.getReport()) {
            double r = metrics.getAvgResTime();
            if (r > 0)
                buffer.append(NUM.format(r));
            else
                buffer.append("N/A");
            buffer.append(',');
        }
        /* Throughput */
        for (Metrics metrics : snapshot.getReport())
            buffer.append(NUM.format(metrics.getThroughput())).append(',');
        /* Bandwidth */
        for (Metrics metrics : snapshot.getReport())
            buffer.append(NUM.format(metrics.getBandwidth())).append(',');
        /* Success Ratio */
        for (Metrics metrics : snapshot.getReport()) {
            double t = (double) metrics.getTotalSampleCount();
            if (t > 0)
                buffer.append(RATIO.format(metrics.getSampleCount() / t));
            else
                buffer.append("N/A");
            buffer.append(',');
        }
        /* Version Info */
        buffer.append(snapshot.getMinVersion()).append(',');
        buffer.append(snapshot.getVersion()).append(',');
        buffer.append(snapshot.getMaxVersion()).append('\n');
        writer.write(buffer.toString());
    }

}
