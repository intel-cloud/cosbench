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

package com.intel.cosbench.controller.loader;

import java.io.BufferedReader;
import java.io.IOException;

import com.intel.cosbench.model.WorkloadInfo;

/**
 * This class is a wrapper to construct different exporters by providing static construction methods.
 * 
 * @author ywang19, qzheng7
 *
 */
public class Loaders {

	public static RunLoader newRunExporter(BufferedReader reader)
			throws IOException {
		AbstractRunLoader loader = new CSVRunLoader(reader);
		return loader;
	}
    
	public static WorkloadFileLoader newWorkloadLoader(BufferedReader reader,
			WorkloadInfo workloadContext) throws IOException {
		AbstractWorkloadFileLoader loader = new CSVWorkloadFileLoader(reader,
				workloadContext);
		return loader;
	}

	public static SnapshotLoader newSnapshotLoader(BufferedReader reader,
			WorkloadInfo workload, String stageId) throws IOException {
		AbstractSnapshotLoader loader = new CSVSnapshotLoader(reader, workload,
				stageId);
		return loader;
	}

}
