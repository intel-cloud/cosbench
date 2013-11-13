package com.intel.cosbench.controller.loader;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import com.intel.cosbench.model.WorkloadInfo;

public interface RunLoader {

	public void init(BufferedReader reader) throws IOException;

	public List<WorkloadInfo> load() throws IOException;

}
