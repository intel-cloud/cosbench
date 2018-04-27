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

import com.intel.cosbench.bench.TaskReport;
import com.intel.cosbench.model.*;

/**
 * This class is a wrapper to construct different exporters by providing static construction methods.
 * 
 * @author ywang19, qzheng7 
 *
 */
public class Exporters {
	
	public static WorkerExporter newWorkExporter(StageInfo info){
		AbstractWorkerExporter exporter = new CSVWorkerExporter();
		exporter.setStageInfo(info);
		return exporter;
	}
	
	public static TaskExporter newTaskExporter(WorkloadInfo workloadInfo,DriverInfo driverInfo){
		AbstractTaskExporter exporter = new CSVTaskExporter();
        exporter.setWorkloadInfo(workloadInfo);
        exporter.setDriverInfo(driverInfo);
        return exporter;
	}

    public static RunExporter newRunExporter(WorkloadInfo workload) {
        AbstractRunExporter exporter = new CSVRunExporter();
        exporter.setWorkload(workload);
        return exporter;
    }

    public static StageExporter newStageExporter(StageInfo stage) {
        AbstractStageExporter exporter = new CSVStageExporter();
        exporter.setStage(stage);
        return exporter;
    }

    public static WorkloadExporter newWorkloadExporter(WorkloadInfo workload) {
        AbstractWorkloadExporter exporter = new CSVWorkloadExporter();
        exporter.setWorkload(workload);
        return exporter;
    }

    public static LatencyExporter newLatencyExporter(WorkloadInfo workload) {
        AbstractLatencyExporter exporter = new CSVLatencyExporter();
        exporter.setWorkload(workload);
        return exporter;
    }

    public static LogExporter newLogExporter(WorkloadInfo workload) {
        SimpleLogExporter exporter = new SimpleLogExporter();
        exporter.setWorkload(workload);
        return exporter;
    }
    
    public static LogExporter newScriptLogExporter(WorkloadInfo workload) {
        ScriptsLogExporter exporter = new ScriptsLogExporter();
        exporter.setWorkload(workload);
        return exporter;
    }

    public static MatrixExporter newMatrixExporter(WorkloadInfo workload) {
        AbstractMatrixExporter exporter = new CSVMatrixExporter();
        exporter.setWorkload(workload);
        return exporter;
    }
    
  

}
