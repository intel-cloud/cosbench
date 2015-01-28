package com.intel.cosbench.exporter;

import static com.intel.cosbench.exporter.Formats.NUM;
import static com.intel.cosbench.exporter.Formats.RATIO;

import java.io.IOException;
import java.io.Writer;

import com.intel.cosbench.bench.Metrics;
import com.intel.cosbench.bench.Report;
import com.intel.cosbench.bench.TaskReport;

public class CSVWorkerExporter extends AbstractWorkerExporter{

	@Override
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

	@Override
	  protected void writeMetrics(Writer writer,Metrics metrics)throws IOException {
        StringBuilder buffer = new StringBuilder();
        /*Operation Type*/
        buffer.append(metrics.getOpType()).append(',');
        /*sample Type*/
        buffer.append(metrics.getSampleType()).append(',');
        /* Operation Count */
        buffer.append(metrics.getSampleCount()).append(',');
        /* Byte Count */
        buffer.append(metrics.getByteCount()).append(',');
        /* Response Time */
        double r = metrics.getAvgResTime();
        if (r > 0)
            buffer.append(NUM.format(r));
        else
            buffer.append("N/A");
        buffer.append(',');
        
        /* Transfer Time */
        double pt = metrics.getAvgResTime() - metrics.getAvgXferTime();
        if (pt > 0)
            buffer.append(NUM.format(pt));
        else
            buffer.append("N/A");
        buffer.append(',');
        
        /* Throughput */
        buffer.append(NUM.format(metrics.getThroughput())).append(',');
        /* Bandwidth */
        buffer.append(NUM.format(metrics.getBandwidth())).append(',');
        /* Success Ratio */
        double t = (double) metrics.getRatio();
        if (t > 0)
            buffer.append(RATIO.format(metrics.getRatio()));
        else
            buffer.append("N/A");
        buffer.append('\n');
        writer.write(buffer.toString());
    }
	
}
