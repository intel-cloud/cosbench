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

import static com.intel.cosbench.controller.loader.Formats.DATETIME;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.intel.cosbench.bench.Report;
import com.intel.cosbench.config.Workload;
import com.intel.cosbench.controller.model.WorkloadContext;
import com.intel.cosbench.model.WorkloadInfo;
import com.intel.cosbench.model.WorkloadState;


class CSVRunLoader extends AbstractRunLoader {

	private List<WorkloadInfo> workloads = new ArrayList<WorkloadInfo>();
	
    public CSVRunLoader(BufferedReader reader) throws IOException {
        super.init(reader);
    }
    
	@Override
	protected void readHeader() throws IOException {
		this.reader.readLine();		
	}

	@Override
	protected List<WorkloadInfo> readWorkload() throws IOException {
		String workloadRecordLine = null;
		while ((workloadRecordLine = this.reader.readLine()) != null) {
			String[] columns = workloadRecordLine.split(",");
			WorkloadContext workloadContext = new WorkloadContext();
			workloadContext.setArchived(true);
			workloadContext.setId(columns[0]);
			Report report = new Report();
			workloadContext.setReport(report);
			Workload workload = new Workload();
			workload.setName(columns[1]);
			workloadContext.setWorkload(workload);
			try {
				if(!columns[2].isEmpty())
					workloadContext.setSubmitDate(DATETIME.parse(columns[2]));
				if(!columns[3].isEmpty())
					workloadContext.setStartDate(DATETIME.parse(columns[3]));
				if(!columns[4].isEmpty())
					workloadContext.setStopDate(DATETIME.parse(columns[4]));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			workloadContext.setOpInfo(columns[5].split(" "));
			for (WorkloadState state : WorkloadState.values()){
				if(columns[6].equalsIgnoreCase(state.toString().toLowerCase())){
					workloadContext.setState(state);
					break;
				}
			}
			int index = 6;
			while(++index <= columns.length-1) {
				String str[] = columns[index].split("@");
				String stateName = str[0].trim();
				Date stateDate = null;
				try {
					if(str.length > 1)
						stateDate = DATETIME.parse(str[1].trim());
				} catch (ParseException e) {
					e.printStackTrace();
				}
				workloadContext.setState(stateName, stateDate);
			}
			workloads.add(workloadContext);
		}
		return workloads;
	}

}
