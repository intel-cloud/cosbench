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

import static com.intel.cosbench.bench.Counter.*;
import static com.intel.cosbench.bench.Metrics.newMetrics;

import java.util.*;

/**
 * This class encapsulates operations to aggregate data from all drivers, 
 * also includes operations to calculate response time distribution.
 * 
 * @author ywang19, qzheng7
 *
 */
public class Aggregator {

    private String type; /* metrics type */

    private int sampleCount = 0; /* successful samples */
    private int totalSampleCount = 0;/* total sample collected */

    private long byteCount = 0L; /* total bytes transferred */
    private int workerCount = 0; /* total workers involved */

    /* children metrics */
    private boolean containsLatency = false;
    private List<Metrics> children = new ArrayList<Metrics>();

    public Aggregator(String type) {
        this.type = type;
    }

    public void addMetrics(Metrics metrics) {
        sampleCount += metrics.getSampleCount();
        totalSampleCount += metrics.getTotalSampleCount();
        byteCount += metrics.getByteCount();
        workerCount += metrics.getWorkerCount();
        children.add(metrics);
        containsLatency = containsLatency || metrics.getLatency() != null;
    }

    public Metrics aggregate() {
        Metrics metrics = newMetrics(type);
        metrics.setSampleCount(sampleCount);
        metrics.setTotalSampleCount(totalSampleCount);
        metrics.setByteCount(byteCount);
        metrics.setWorkerCount(workerCount);
        metrics.setThroughput(getThroughput());
        metrics.setBandwidth(getBandwidth());
        metrics.setAvgResTime(getAvgResTime());
        metrics.setAvgXferTime(getAvgXferTime());
        metrics.setLatency(getLatency());
		metrics.setRatio(metrics.getTotalSampleCount() > 0 ? (double) metrics
				.getSampleCount() / metrics.getTotalSampleCount() : 0D);
        return metrics;
    }

    private double getThroughput() {
        double sum = 0D;
        for (Metrics metrics : children)
            sum += metrics.getThroughput();
        return sum;
    }

    private double getBandwidth() {
        double sum = 0D;
        for (Metrics metrics : children)
            sum += metrics.getBandwidth();
        return sum;
    }

    private double getAvgResTime() {
        if (sampleCount == 0)
            return 0D;
        double sum = 0D;
        for (Metrics metrics : children)
            sum += metrics.getAvgResTime() * metrics.getSampleCount();
        return sum / sampleCount;
    }

    private double getAvgXferTime() {
		if (sampleCount == 0)
			return 0D;
		double sum = 0D;
		for (Metrics metrics : children)
			sum += metrics.getAvgXferTime() * metrics.getSampleCount();
		return sum / sampleCount;
	}

    private Histogram getLatency() {
        if (!containsLatency)
            return null;
        Histogram histogram = new Histogram();
        histogram.setHistoData(getLatencyHistoData());
        histogram.recalcPercentiles();
        return histogram;
    }

    private int[] getLatencyHistoData() {
        int size = 1 + (int) (RES_MAX / RES_INT);
        int[] data = new int[size];
        for (Metrics metrics : children)
            if (metrics.getLatency() != null)
                for (int i = 0; i < size; i++)
                    data[i] += metrics.getLatency().getHistoData()[i];
        return data;
    }

}
