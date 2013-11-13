package com.intel.cosbench.controller.loader;

import java.io.BufferedReader;
import java.io.IOException;

import com.intel.cosbench.model.WorkloadInfo;

public interface SnapshotLoader {

	void init(BufferedReader reader, WorkloadInfo workloadContext,
			String stageId) throws IOException;	
	
	public void load() throws IOException;

}
