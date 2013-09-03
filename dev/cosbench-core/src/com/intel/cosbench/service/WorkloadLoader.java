package com.intel.cosbench.service;

import java.io.IOException;
import java.util.List;

import com.intel.cosbench.model.WorkloadInfo;

public interface WorkloadLoader {

	public List<WorkloadInfo> loadWorkloadRun() throws IOException;

	public void loadWorkloadPageInfo(WorkloadInfo workloadContext) throws IOException;
	
	public void loadStagePageInfo(WorkloadInfo workloadContext, String stageId) throws IOException;
}
