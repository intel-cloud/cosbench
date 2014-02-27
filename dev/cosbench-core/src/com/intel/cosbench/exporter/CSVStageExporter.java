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

import org.apache.commons.lang.StringUtils;

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
        buffer.append("Avg-ProcTime").append(suffix);
        buffer.append("Throughput").append(suffix);
        buffer.append("Bandwidth").append(suffix);
        buffer.append("Succ-Ratio").append(suffix);
        buffer.append("Version-Info");
        buffer.append(',').append(',').append('\n').append(',');
        for (int i = 0; i < 7; i++)
            // 7 metrics
            for (Metrics metrics : snapshots[0].getReport())
				buffer.append(
						StringUtils.join(new Object[] {
								(metrics.getOpName().equals(
										metrics.getSampleType()) ? null
										: metrics.getOpName() + "-"),
								metrics.getSampleType() })).append(',');
        buffer.append("Min-Version").append(','); 
        buffer.append("Version").append(',');
        buffer.append("Max-Version").append('\n');
        writer.write(buffer.toString());
    }

    protected void writeMetrics(Writer writer, Snapshot snapshot)
            throws IOException {
        StringBuilder buffer = new StringBuilder();
        buffer.append(TIME.format(snapshot.getTimestamp())).append(',');
        Report report = snapshot.getReport();

        if(report.getSize() == 0)
        {
               report.addMetrics(Metrics.newMetrics("na.na"));
        }
        
        /* Operation Count */
        for (Metrics metrics : report)
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
            buffer.append(',');
        }
        /* Version Info */
        buffer.append(snapshot.getMinVersion()).append(',');
        buffer.append(snapshot.getVersion()).append(',');
        buffer.append(snapshot.getMaxVersion()).append('\n');
        writer.write(buffer.toString());
    }

}
