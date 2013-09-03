package com.intel.cosbench.controller.loader;

import java.io.BufferedReader;
import java.io.IOException;

import com.intel.cosbench.model.WorkloadInfo;

public interface WorkloadFileLoader {

	void init(BufferedReader reader, WorkloadInfo workloadContext) throws IOException;
	
	public void load() throws IOException;



}
