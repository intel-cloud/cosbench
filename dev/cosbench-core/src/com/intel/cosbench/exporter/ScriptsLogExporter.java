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

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.intel.cosbench.model.DriverInfo;
import com.intel.cosbench.model.StageInfo;
import com.intel.cosbench.model.WorkloadInfo;

public class ScriptsLogExporter implements LogExporter {

    private WorkloadInfo workload;

    public ScriptsLogExporter() {
        /* empty */
    }

    public void setWorkload(WorkloadInfo workload) {
        this.workload = workload;
    }

    @Override
    public void export(Writer writer) throws IOException {
    	exportWorkloadLog(writer);
    	for (StageInfo stage : workload.getStageInfos())
            exportStageLog(writer, stage);
        writer.flush();
    }
    
    private void exportWorkloadLog(Writer writer) throws IOException {
        writer.write("=========================");
        writer.write("=========================");
        writer.write(" workload level " );
        writer.write("=========================");
        writer.write("=========================");
        writer.write('\n');
        String wsId = workload.getId();
        doExportLog(writer, wsId);
        writer.flush();
	}
    
    private void doExportLog(Writer writer, String wsId) throws IOException {
        for (DriverInfo driver : workload.getDriverInfos()) {
        	Map<String, String> logMap = driver.getLogMap();
        	if (logMap.containsKey(wsId)) {
				exportScriptLog(writer, logMap.get(wsId), driver.getName());
			}
        }
	}

    private void exportStageLog(Writer writer, StageInfo stage) throws IOException {
        writer.write("=========================");
        writer.write("=========================");
        writer.write(" stage: " + stage.getId() + ' ');
        writer.write("=========================");
        writer.write("=========================");
        writer.write('\n');
        String wsId = workload.getId()+stage.getId();
        doExportLog(writer, wsId);
        writer.flush();
    }
    
    private void exportScriptLog(Writer writer, String logCtx, String driver) throws IOException {
		int idx = StringUtils.indexOf(logCtx, ";");
		if (idx < 0 || idx+1 == logCtx.length())
			return;
		String scriptName = StringUtils.left(logCtx, idx);
		String log = StringUtils.substring(logCtx, idx+1);
        writer.write("-----------------");
        writer.write("-----------------");
        writer.write(" driver: " + driver + "  script: " + scriptName + ' ');
        writer.write("-----------------");
        writer.write("-----------------");
        writer.write('\n');
		writer.write(log);
	}

}
