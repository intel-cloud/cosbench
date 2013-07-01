package com.intel.cosbench.driver.util;

import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;

import com.intel.cosbench.api.storage.StorageInterruptedException;
import com.intel.cosbench.bench.Result;
import com.intel.cosbench.bench.Sample;
import com.intel.cosbench.driver.agent.WorkAgent;
import com.intel.cosbench.driver.operator.OperationListener;
import com.intel.cosbench.service.AbortedException;

public class StatsCallback {

	private OperationListener workAgent;
	private String opType;

	public StatsCallback(OperationListener workAgent) {
		this.workAgent = workAgent;
	}

	public void setOpType(String type) {
		this.opType = type;
	}

	public Sample onStats(HttpResponse response, long time, boolean status) {
		Date now = new Date();
		Sample sample = new Sample(now, opType, true, time, response
				.getEntity().getContentLength());
		workAgent.onSampleCreated(sample);
		Result result = new Result(now, opType, status);
		workAgent.onOperationCompleted(result);
		return null;
	}

}
