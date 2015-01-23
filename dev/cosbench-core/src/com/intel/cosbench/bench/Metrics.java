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

package com.intel.cosbench.bench;

import com.intel.cosbench.utils.MapRegistry.Item;


/**
 * The class represents the overall performance metrics per each type.
 * 
 * @author ywang19, qzheng7
 *
 */
public class Metrics implements Item, Cloneable {

    private String name; /* metrics id */

    /* Type */

    private String opType; /* operation type */
    private String sampleType; /* sample type */
    private String opName; /* operation name*/
    private String opId; /* operation id */

    /* Status */

    private int sampleCount; /* number of successful samples */
    private int totalSampleCount; /* total operations issued */
    private long byteCount; /* total bytes transferred */
    private int workerCount; /* total workers involved */

    /* Metrics */
    
    private double avgResTime; /* average response time */
    private double avgXferTime; /* average transfer time */
    private double throughput; /* operation throughput */
    private double bandwidth; /* network bandwidth */

    /* Latency Details */
    private Histogram latency; /* detailed latency metrics */
    
    /* success ratio */
    private double ratio;

    public Metrics() {
        /* empty */
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOpType() {
        return opType;
    }

    public void setOpType(String opType) {
        this.opType = opType;
    }

    public String getSampleType() {
        return sampleType;
    }

    public void setSampleType(String sampleType) {
        this.sampleType = sampleType;
    }
    
    public String getOpName(){
    	return opName;
    }
    
    public void setOpName(String opName){
    	this.opName = opName;
    }
    
    public String getOpId() {
    	return opId;
    }
    
    public void setOpId(String opId) {
    	this.opId = opId;
    }

    public int getSampleCount() {
        return sampleCount;
    }

    public void setSampleCount(int sampleCount) {
        this.sampleCount = sampleCount;
    }

    public int getTotalSampleCount() {
        return totalSampleCount;
    }

    public void setTotalSampleCount(int totalSampleCount) {
        this.totalSampleCount = totalSampleCount;
    }

    public long getByteCount() {
        return byteCount;
    }

    public void setByteCount(long byteCount) {
        this.byteCount = byteCount;
    }

    public int getWorkerCount() {
        return workerCount;
    }

    public void setWorkerCount(int workerCount) {
        this.workerCount = workerCount;
    }

    public double getAvgResTime() {
        return avgResTime;
    }

    public void setAvgResTime(double avgResTime) {
        this.avgResTime = avgResTime;
    }

    public double getAvgXferTime() {
    	return avgXferTime;
    }

    public void setAvgXferTime(double avgXferTime) {
    	this.avgXferTime = avgXferTime;
    }
    
    public double getThroughput() {
        return throughput;
    }

    public void setThroughput(double throughput) {
        this.throughput = throughput;
    }

    public double getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(double bandwidth) {
        this.bandwidth = bandwidth;
    }

    public Histogram getLatency() {
        return latency;
    }

    public void setLatency(Histogram latency) {
        this.latency = latency;
    }
    
    public void setRatio(double ratio) {
    	this.ratio = ratio;
    }
    
    public double getRatio() {
    	return ratio;
    }

    @Override
    public Metrics clone() {
        try {
            return (Metrics) super.clone();
        } catch (CloneNotSupportedException e) {
        }
        return this;
    }

	public static String getMetricsType(String opId, String opType,
			String sampleType, String opName) {
		return opId + "." + opType + "." + sampleType + "." + opName;
    }

    public static Metrics newMetrics(String type) {
        String[] types = type.split("\\.");
        Metrics metrics = new Metrics();
        metrics.setName(type);
        metrics.setOpId(types.length > 0? types[0] : "na");
        metrics.setOpType(types.length > 1? types[1] : "na");
        metrics.setSampleType(types.length > 2? types[2] : "na");
        metrics.setOpName(types.length > 3? types[3] : "na");
        return metrics;
    }

    public static Metrics convert(Mark mark, long window) {
        int sps = mark.getSampleCount();
        int tsps = mark.getTotalSampleCount();
        long rtSum = mark.getRtSum();
        long xtSum = mark.getXtSum();
        long bytes = mark.getByteCount();
		String type = getMetricsType(mark.getOpId(), mark.getOpType(),
				mark.getSampleType(), mark.getOpName());
        Metrics metrics = newMetrics(type);
        metrics.setSampleCount(sps);
        metrics.setTotalSampleCount(tsps);
        metrics.setRatio(metrics.getTotalSampleCount() > 0 ? (double) metrics
				.getSampleCount() / metrics.getTotalSampleCount() : 0D);
        metrics.setByteCount(bytes);
        metrics.setWorkerCount(1);
        metrics.setAvgResTime(rtSum > 0 ? ((double) rtSum) / sps : 0);
        metrics.setAvgXferTime(xtSum > 0 ? ((double) xtSum) / sps : 0);
        metrics.setThroughput(sps > 0 ? ((double) sps) / window * 1000 : 0);
        metrics.setBandwidth(bytes > 0 ? ((double) bytes) / window * 1000 : 0);
        return metrics;
    }
}
