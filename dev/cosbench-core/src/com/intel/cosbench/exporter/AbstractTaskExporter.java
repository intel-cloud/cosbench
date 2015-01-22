package com.intel.cosbench.exporter;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import com.intel.cosbench.bench.Snapshot;
import com.intel.cosbench.bench.TaskReport;
import com.intel.cosbench.model.DriverInfo;
import com.intel.cosbench.model.StageInfo;
import com.intel.cosbench.model.WorkloadInfo;

public abstract class AbstractTaskExporter implements TaskExporter {

    protected WorkloadInfo workloadInfo;
    protected DriverInfo driverInfo;
    protected List<TaskReport> driverTasks = new ArrayList<TaskReport>(); 
    
	public WorkloadInfo getWorkloadInfo() {
		return workloadInfo;
	}

	public void setWorkloadInfo(WorkloadInfo workloadInfo) {
		this.workloadInfo = workloadInfo;
	}

	public DriverInfo getDriverInfo() {
		return driverInfo;
	}

	public void setDriverInfo(DriverInfo driverInfo) {
		this.driverInfo = driverInfo;
	}
	
	
	 @Override
    public void export(Writer writer) throws IOException {
		 //check every stage every task   
		for(StageInfo sInfo:workloadInfo.getStageInfos()){
			for(TaskReport tReport:sInfo.getTaskReports()){
				if(tReport.getDriverUrl().equals(driverInfo.getUrl())){
					driverTasks.add(tReport);
				}
			}
		}
        writeReport(writer);
    }

    private void writeReport(Writer writer) throws IOException {
        writeHeader(writer);
        writer.flush();
        for (TaskReport tReport : driverTasks)
            writeMetrics(writer, tReport);
        writer.flush();
    }

    protected abstract void writeHeader(Writer writer) throws IOException;

    protected abstract void writeMetrics(Writer writer, TaskReport tReport)
            throws IOException;
}
