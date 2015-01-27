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
