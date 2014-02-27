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
import java.util.Date;

import com.intel.cosbench.bench.Histogram;
import com.intel.cosbench.bench.Metrics;
import com.intel.cosbench.bench.Report;
import com.intel.cosbench.model.StageState;
import com.intel.cosbench.model.WorkloadInfo;

class CSVWorkloadFileLoader extends AbstractWorkloadFileLoader {

	public CSVWorkloadFileLoader(BufferedReader reader,
			WorkloadInfo workloadContext) throws IOException {
		super.init(reader, workloadContext);
	}

	@Override
	protected void readHeader() throws IOException {
		this.reader.readLine();
	}

	@Override
	protected void readWorkload() throws IOException {
		String workloadRecordLine = null;
		String lastStageName = null;
		String stageId = null;
		boolean sameStage = false;
		int index = 1;
		while ((workloadRecordLine = this.reader.readLine()) != null) {
			String[] columns = workloadRecordLine.split(",");
			sameStage = true;
			if (lastStageName == null
					|| !lastStageName.equalsIgnoreCase(columns[0])) {
				lastStageName = columns[0];
				stageId = "s" + index++;
				sameStage = false;
			}
			if (columns[16].equalsIgnoreCase("completed")) {
				Metrics metrics = loadMetrics(columns);
				if (!sameStage) {
					Report report = new Report();
					workloadContext.getStageInfo(stageId).setReport(report);
				}
				workloadContext.getStageInfo(stageId).getReport()
						.addMetrics(metrics);
				workloadContext.getReport().addMetrics(metrics);
			}
			for (StageState state : StageState.values()) {
				if (columns[16]
						.equalsIgnoreCase(state.toString().toLowerCase())) {
					workloadContext.getStageInfo(stageId).setState(state, true);
					break;
				}
			}
			int pos = 16;
			while (!sameStage && ++pos <= columns.length - 1) {
				String str[] = columns[pos].split("@");
				String stateName = str[0].trim();
				Date stateDate = null;
				try {
					stateDate = DATETIME.parse(str[1].trim());
				} catch (ParseException e) {
					e.printStackTrace();
				}
				workloadContext.getStageInfo(stageId).setState(stateName,
						stateDate);
			}
		}
	}

	private Metrics loadMetrics(String[] columns) {
		Metrics metrics = new Metrics();
		int n = columns[1].lastIndexOf("-");
		metrics.setName(columns[1]);
		if (n > 0) {
			metrics.setOpName(columns[1].substring(0, n));
			metrics.setSampleType(columns[1].substring(n + 1));
		} else {
			metrics.setOpName(columns[1]);
			metrics.setSampleType(columns[1]);
		}
		metrics.setOpType(columns[2]);
		metrics.setSampleCount(Integer.valueOf(columns[3]));
		metrics.setByteCount(Long.valueOf(columns[4]));
		double rt = columns[5].equalsIgnoreCase("N/A") ? 0 : Double.valueOf(columns[5]);
		metrics.setAvgResTime(rt);
		double pt = columns[6].equalsIgnoreCase("N/A") ? 0 : Double.valueOf(columns[6]);
		metrics.setAvgXferTime(rt - pt);
		metrics.setLatency(loadHistogram(columns));
		metrics.setThroughput(Double.valueOf(columns[13]));
		metrics.setBandwidth(Double.valueOf(columns[14]));
		setRatio(columns[15], metrics);
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

	private Histogram loadHistogram(String[] columns) {
		Histogram histogram = new Histogram();
		long[] l_60 = new long[2];
		l_60[1] = columns[7].equalsIgnoreCase("N/A") ? 0L : Long
				.valueOf(columns[7]);
		histogram.set_60(l_60);
		long[] l_80 = new long[2];
		l_80[1] = columns[8].equalsIgnoreCase("N/A") ? 0L : Long
				.valueOf(columns[8]);
		histogram.set_80(l_80);
		long[] l_90 = new long[2];
		l_90[1] = columns[9].equalsIgnoreCase("N/A") ? 0L : Long
				.valueOf(columns[9]);
		histogram.set_90(l_90);
		long[] l_95 = new long[2];
		l_95[1] = columns[10].equalsIgnoreCase("N/A") ? 0L : Long
				.valueOf(columns[10]);
		histogram.set_95(l_95);
		long[] l_99 = new long[2];
		l_99[1] = columns[11].equalsIgnoreCase("N/A") ? 0L : Long
				.valueOf(columns[11]);
		histogram.set_99(l_99);
		long[] l_100 = new long[2];
		l_100[1] = columns[12].equalsIgnoreCase("N/A") ? 0L : Long
				.valueOf(columns[12]);
		histogram.set_100(l_100);
		return histogram;
	}

}
