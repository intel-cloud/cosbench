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

package com.intel.cosbench.driver.model;

import java.util.*;
import java.util.concurrent.Future;

import com.intel.cosbench.bench.*;
import com.intel.cosbench.config.*;
import com.intel.cosbench.config.common.KVConfigParser;
import com.intel.cosbench.driver.util.OperationPicker;
import com.intel.cosbench.log.LogManager;
import com.intel.cosbench.model.*;

/**
 * This class encapsulates behaviors of mission scheduled to current driver, one
 * mission is actually fragment of one workload.
 * 
 * @author ywang19, qzheng7
 * 
 */
public class MissionContext implements MissionInfo {

    private String id;
    private Date date;
    private volatile MissionState state;
    private StateRegistry stateHistory = new StateRegistry();
    private transient XmlConfig config;
    private transient volatile Future<?> future;
    
    private Mission mission;
    private LogManager logManager;
    private ErrorStatistics errorStatistics;
    private transient OperationPicker operationPicker;
    private transient OperatorRegistry operatorRegistry;
    
    private WorkerRegistry workerRegistry;

    /* Report will be available after the mission is accomplished */
    private volatile Report report = null; // will be merged from worker reports

    private transient List<MissionListener> listeners = new ArrayList<MissionListener>();
    
    private static final String GENERATE_HISTOGRAM_KEY = "histogram";
    private static final boolean DEFAULT_GENERATE_HISTOGRAM = true;
    
    public List<Report> getWorkerReports(){
    	List<Report> wReports = new ArrayList<Report>();
    	for(WorkerContext wContext:workerRegistry){
    		wReports.add(wContext.getReport());
    	}
    	return wReports;
    }
    
    public MissionContext() {
        errorStatistics = new ErrorStatistics();
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public MissionState getState() {
        return state;
    }

    public void setState(MissionState state) {
        this.state = state;
        stateHistory.addState(state.name());
        if (MissionState.isStopped(state) && !state.equals(MissionState.FAILED))
            fireMissionStopped();
    }

    private void fireMissionStopped() {
        if (report == null)
            report = mergeReport();
        for (MissionListener listener : listeners)
            listener.missionStopped(this);
    }
    
    private Report mergeReport() {
        ReportMerger merger = new ReportMerger();
        for (WorkerContext worker : workerRegistry)
            merger.add(worker.getReport());
        Report report = merger.merge();
        Config missionConfig = KVConfigParser.parse(mission.getConfig());
        boolean histogram = missionConfig.getBoolean(GENERATE_HISTOGRAM_KEY, DEFAULT_GENERATE_HISTOGRAM);
        if(histogram) {
        	generateHistogram(report);
        }
        return report;
    }
    
    private void generateHistogram(Report report) {
        OperatorRegistry registry = operatorRegistry;
        for (Metrics metrics : report) {
            OperatorContext op = registry.getOperator(metrics.getOpId());
            metrics.setLatency(Histogram.convert(op.getCounter()));
        }
    }
    @Override
    public StateInfo[] getStateHistory() {
        return stateHistory.getAllStates();
    }

    public XmlConfig getConfig() {
        return config;
    }

    public void setConfig(XmlConfig config) {
        this.config = config;
    }

    public Future<?> getFuture() {
        return future;
    }

    public void setFuture(Future<?> future) {
        this.future = future;
    }

    @Override
    public Mission getMission() {
        return mission;
    }

    public void setMission(Mission mission) {
        this.mission = mission;
    }

    @Override
    public LogManager getLogManager() {
        return logManager;
    }

    public void setLogManager(LogManager logManager) {
        this.logManager = logManager;
    }
    
    public ErrorStatistics getErrorStatistics() {
		return errorStatistics;
	}

	public void setErrorStatistics(ErrorStatistics errorStatistics) {
		this.errorStatistics = errorStatistics;
	}

	public OperationPicker getOperationPicker() {
        return operationPicker;
    }

    public void setOperationPicker(OperationPicker operationPicker) {
        this.operationPicker = operationPicker;
    }

    public OperatorRegistry getOperatorRegistry() {
        return operatorRegistry;
    }

    public void setOperatorRegistry(OperatorRegistry operatorRegistry) {
        this.operatorRegistry = operatorRegistry;
    }

    public WorkerRegistry getWorkerRegistry() {
        return workerRegistry;
    }

    @Override
    public int getWorkerCount() {
        return workerRegistry.getSize();
    }

    @Override
    public WorkerInfo[] getWorkerInfos() {
        return workerRegistry.getAllWorkers();
    }

    public void setWorkerRegistry(WorkerRegistry workerRegistry) {
        this.workerRegistry = workerRegistry;
    }

    @Override
    public Snapshot getSnapshot() {
        SnapshotMerger merger = new SnapshotMerger();
        for (WorkerContext worker : workerRegistry)
            merger.add(worker.getSnapshot());
        return merger.merge();
    }

    @Override
    public Report getReport() {
        return report != null ? report : new Report();
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public void addListener(MissionListener listener) {
        listeners.add(listener);
    }

    @Override
    public void disposeRuntime() {
    	if(MissionState.isStopped(state)) {
	        for (WorkerContext worker : workerRegistry)
	            worker.disposeRuntime();
	        config = null;
	        future = null;
	        operationPicker = null;
	        operatorRegistry = null;
	        listeners = null;
	        logManager.dispose();
    	}
    }

}
