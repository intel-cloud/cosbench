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

import static com.intel.cosbench.exporter.Formats.NUM;
import static com.intel.cosbench.exporter.Formats.RATIO;
import static com.intel.cosbench.exporter.Formats.TIME;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;

import com.intel.cosbench.bench.Metrics;
import com.intel.cosbench.bench.Report;
import com.intel.cosbench.bench.Snapshot;
import com.intel.cosbench.bench.TaskReport;

public class CSVTaskExporter extends AbstractTaskExporter{

    public CSVTaskExporter() {
        /* empty */
    }
    protected void writeHeader(Writer writer) throws IOException {
        StringBuilder buffer = new StringBuilder();
        char[] cs = new char[8];
        buffer.append("Op-Type").append(',');
        buffer.append("Sample-Type").append(',');
        buffer.append("Op-Count").append(',');
        buffer.append("Byte-Count").append(',');
        buffer.append("Avg-ResTime").append(',');
        buffer.append("Avg-ProcTime").append(',');
        buffer.append("Throughput").append(',');
        buffer.append("Bandwidth").append(',');
        buffer.append("Succ-Ratio").append('\n');
        writer.write(buffer.toString());
    }

    protected void writeMetrics(Writer writer,TaskReport tReport)throws IOException {
        StringBuilder buffer = new StringBuilder();
        Report report = tReport.getReport();
        /*Operation Type*/
        for(Metrics metrics:report)
            buffer.append(metrics.getOpType()).append(',');
        /*sample Type*/
        for(Metrics metrics:report)
            buffer.append(metrics.getSampleType()).append(',');
        /* Operation Count */
        for (Metrics metrics :report)
            buffer.append(metrics.getSampleCount()).append(',');
        /* Byte Count */
        for (Metrics metrics : report)
            buffer.append(metrics.getByteCount()).append(',');
        /* Response Time */
        for (Metrics metrics : report) {
            double r = metrics.getAvgResTime();
            if (r > 0)
                buffer.append(NUM.format(r));
            else
                buffer.append("N/A");
            buffer.append(',');
        }
        /* Transfer Time */
        for (Metrics metrics : report) {
            double pt = metrics.getAvgResTime() - metrics.getAvgXferTime();
            if (pt > 0)
                buffer.append(NUM.format(pt));
            else
                buffer.append("N/A");
            buffer.append(',');
        }
        /* Throughput */
        for (Metrics metrics : report)
            buffer.append(NUM.format(metrics.getThroughput())).append(',');
        /* Bandwidth */
        for (Metrics metrics : report)
            buffer.append(NUM.format(metrics.getBandwidth())).append(',');
        /* Success Ratio */
        for (Metrics metrics : report) {
            double t = (double) metrics.getRatio();
            if (t > 0)
                buffer.append(RATIO.format(metrics.getRatio()));
            else
                buffer.append("N/A");
            buffer.append('\n');
        }
        writer.write(buffer.toString());
    }
}
