package com.intel.cosbench.driver.util;

import java.util.Date;

import com.intel.cosbench.bench.Result;
import com.intel.cosbench.bench.Sample;
import com.intel.cosbench.driver.agent.WorkAgent;

public class StatsCallback {
	
	private WorkAgent workAgent;
	private String opType;
	
	public StatsCallback(WorkAgent workAgent){
		this.workAgent = workAgent;  
	}
	
	public void setOpType(String type){
		this.opType = type;
	}
	
	public Sample onStats(long time, boolean status){
		Date now = new Date();
		Sample sample = new Sample(now, opType, true, time, 0L);// no set byte count
		workAgent.onSampleCreated(sample);
        Result result = new Result(now, opType, status);
        workAgent.onOperationCompleted(result);
		return null;
	}

}
