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

import static com.intel.cosbench.controller.loader.Formats.TIME;
import static com.intel.cosbench.controller.loader.Formats.getDoubleValue;
import static com.intel.cosbench.controller.loader.Formats.getIntValue;
import static com.intel.cosbench.controller.loader.Formats.getLongValue;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.intel.cosbench.bench.Metrics;
import com.intel.cosbench.bench.Report;
import com.intel.cosbench.bench.Snapshot;
import com.intel.cosbench.config.Work;
import com.intel.cosbench.model.WorkloadInfo;

class CSVSnapshotLoader extends AbstractSnapshotLoader {

	private String[] name = null;
	private int opNum = 0;

	public CSVSnapshotLoader(BufferedReader reader,
			WorkloadInfo workloadContext, String stageId) throws IOException {
		super.init(reader, workloadContext, stageId);
	}

	@Override
	protected void readHeader() throws IOException {
		if(this.reader.readLine() == null){
			return;
		}
		String workloadRecordLine = this.reader.readLine();
		String[] columns = workloadRecordLine.split(",");
		for (Work work : stageContext.getStage().getWorks()) {
			opNum += work.getOperations().size();
		}
		name = new String[opNum];
		for (int i = 0; i < opNum; i++)
			name[i] = columns[i + 1];
	}

	@Override
	protected void readSnapshot() throws IOException {
		String workloadRecordLine = null;
		while ((workloadRecordLine = this.reader.readLine()) != null) {
			String[] columns = workloadRecordLine.split(",");
			Date timestamp = null;
			try {
				timestamp = TIME.parse(columns[0]);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			Snapshot snapshot = new Snapshot(loadReport(columns), timestamp);
			snapshot.setMinVersion(Integer.valueOf(columns[1 + opNum * 7]));
			snapshot.setVersion(Integer.valueOf(columns[2 + opNum * 7]));
			snapshot.setMaxVersion(Integer.valueOf(columns[3 + opNum * 7]));
			stageContext.getSnapshotRegistry().addItem(snapshot);
		}
	}

	private Report loadReport(String[] columns) {
		Report report = new Report();
		List<Metrics> metrics = loadMetrics(columns);
		for (Metrics metric : metrics)
			report.addMetrics(metric);
		return report;
	}

	private List<Metrics> loadMetrics(String[] columns) {
		List<Metrics> metrics = new ArrayList<Metrics>();
		for (int i = 0; i < opNum; i++) {
			Metrics metric = new Metrics();
			metric.setName(name[i]);
			int n = name[i].lastIndexOf("-");
			if (n > 0) {
				metric.setOpName(name[i].substring(0, n));
				metric.setSampleType(name[i].substring(n + 1));
			} else {
				metric.setOpName(name[i]);
				metric.setSampleType(name[i]);
			}
			metric.setSampleCount(getIntValue(columns[i + 1]));
			metric.setByteCount(getLongValue(columns[i + opNum + 1]));
			double rt = getDoubleValue(columns[i + opNum * 2 + 1]);
			metric.setAvgResTime(rt);
			double pt = getDoubleValue(columns[i + opNum * 3 + 1]);
			metric.setAvgXferTime(rt - pt);
			metric.setThroughput(getDoubleValue(columns[i + opNum * 4 + 1]));
			metric.setBandwidth(getDoubleValue(columns[i + opNum * 5 + 1]));
//			metric.setRatio(columns[i + opNum * 5 + 1].equalsIgnoreCase("N/A") ? 0D
//					: Double.valueOf(columns[i + opNum * 5 + 1].substring(0,
//							columns[i + opNum * 5 + 1].length() - 1)) / 100.0);
			setRatio(columns[i + opNum * 6 + 1], metric);
			metrics.add(metric);
		}
		return metrics;
	}
	
	private void setRatio(String column, Metrics metrics) {
		if (!column.equalsIgnoreCase("N/A")) {
			metrics.setRatio(Double.valueOf(column.substring(0,
					column.length() - 1)) / 100.0);
			metrics.setTotalSampleCount(metrics.getSampleCount()
					/ metrics.getRatio() > Integer.MAX_VALUE ? Integer.MAX_VALUE
					: (int) (metrics.getSampleCount() / metrics.getRatio()));
		} else {
			metrics.setRatio(0D);
			metrics.setTotalSampleCount(0);
		}
	}
}	
