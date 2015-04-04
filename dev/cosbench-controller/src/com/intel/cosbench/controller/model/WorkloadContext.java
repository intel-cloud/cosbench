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

package com.intel.cosbench.controller.model;

import java.util.*;
import java.util.concurrent.Future;

import com.intel.cosbench.bench.*;
import com.intel.cosbench.config.*;
import com.intel.cosbench.log.LogFactory;
import com.intel.cosbench.log.Logger;
import com.intel.cosbench.model.*;

public class WorkloadContext implements WorkloadInfo {

	private static final Logger LOGGER = LogFactory.getSystemLogger();
    private String id;
    private Date submitDate;
    private Date startDate;
    private Date stopDate;
    private volatile WorkloadState state;
    private StateRegistry stateHistory = new StateRegistry();
    private transient XmlConfig config;
    private transient volatile Future<?> future;

    private Workload workload;
    private transient volatile StageInfo currentStage;
    private StageRegistry stageRegistry;
    private int order; /* workload order */
    private DriverRegistry driverRegistry;

    /* Report will be available after the workload is finished */
    private volatile Report report = null; // will be merged from stage reports

    private transient List<WorkloadListener> listeners = new ArrayList<WorkloadListener>();
    
    private String[] opInfo;
    
    private boolean archived = false;
    
 // private HashMap<String, HashMap<String, Integer>> errorStatistics = new HashMap<String, HashMap<String,Integer>>();
    private HashMap<String, ErrorSummary> errorStatistics = new HashMap<String, ErrorSummary>();
    public WorkloadContext() {
        /* empty */
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public int getOrder(){
    	return this.order;
    }
    
    public void setOrder(int order){
    	this.order = order;
    }

    @Override
    public Date getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(Date submitDate) {
        this.submitDate = submitDate;
    }

    @Override
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Override
    public Date getStopDate() {
        return stopDate;
    }

    public void setStopDate(Date stopDate) {
        this.stopDate = stopDate;
    }

    @Override
    public WorkloadState getState() {
        return state;
    }

    public void setState(WorkloadState state) {
        this.state = state;
    	if(this.archived)
    		return;
        stateHistory.addState(state.name());
        if (WorkloadState.isRunning(state))
            fireWorkloadStarted();
        if (WorkloadState.isStopped(state))
            fireWorkloadStopped();
    }
    
    public void setState(String state, Date date){
    	stateHistory.addState(state, date);
    }

    private void fireWorkloadStarted() {
        for (WorkloadListener listener : listeners)
            listener.workloadStarted(this);
    }
    
    @Override
    public void setArchived(boolean archived) {
    	this.archived = archived;
    }
    
    @Override
    public boolean getArchived() {
    	return archived;
    }

    private void fireWorkloadStopped() {
        if (report == null)
            report = mergeReport();
        for (WorkloadListener listener : listeners)
            listener.workloadStopped(this);
    }

    private Report mergeReport() {
        Report report = new Report();
        for (StageContext stage : stageRegistry) {
            int mid = 1;
            for (Metrics metrics : stage.getReport()) {
                Metrics clone = metrics.clone();
                String uuid = id + "-" + stage.getId() + "-" + mid++;
                clone.setName(uuid); // reset metrics name
                report.addMetrics(clone);
            }
        }
        return report;
    }

    @Override
    public StateInfo[] getStateHistory() {
        return stateHistory.getAllStates();
    }

    @Override
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
    public Workload getWorkload() {
        return workload;
    }

    public void setWorkload(Workload workload) {
        this.workload = workload;
    }
    
    public String[] getOpInfo(){
    	return opInfo;
    }
    
    public void setOpInfo(String[] opInfo){
    	this.opInfo = opInfo;
    }

    @Override
    public String[] getAllOperations() {
    	if(opInfo == null) {
        Set<String> ops = new LinkedHashSet<String>();
        for (Stage stage : workload.getWorkflow())
            for (Work work : stage)
                for (Operation op : work)
                    ops.add(op.getType());
        setOpInfo(ops.toArray(new String[ops.size()]));
    	}
        return getOpInfo();
    }

    @Override
    public StageInfo getCurrentStage() {
        return currentStage;
    }

    public void setCurrentStage(StageInfo currentStage) {
        this.currentStage = currentStage;
    }

    public StageRegistry getStageRegistry() {
        return stageRegistry;
    }

    @Override
    public int getStageCount() {
        return stageRegistry.getSize();
    }

    @Override
    public StageInfo getStageInfo(String id) {
        for (StageInfo info : stageRegistry)
            if (info.getId().equals(id))
                return info;
        return null;
    }

    @Override
    public StageInfo[] getStageInfos() {
        return stageRegistry.getAllStages();
    }

    public void setStageRegistry(StageRegistry stageRegistry) {
        this.stageRegistry = stageRegistry;
    }

    @Override
    public int getSnapshotCount() {
        int total = 0;
        for (StageInfo info : stageRegistry)
            total += info.getSnapshotCount();
        return total;
    }

    @Override
    public Snapshot getSnapshot() {
        if (currentStage == null)
            return new Snapshot();
        return currentStage.getSnapshot();
    }

    @Override
    public Report getReport() {
        return report != null ? report : new Report();
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public void addListener(WorkloadListener listener) {
    	if(listeners == null)
    		return;
        listeners.add(listener);
    }
    

    public HashMap<String, ErrorSummary> getErrorStatistics() {
		return errorStatistics;
	}
       
    public void mergeErrorStatistics(){
    	for(StageContext stageContext : stageRegistry){
    		for(TaskContext taskContext : stageContext.getTaskRegistry()){
    			String driverUrl = taskContext.getSchedule().getDriver().getUrl();
    			if (! errorStatistics.containsKey(driverUrl))
    				errorStatistics.put(driverUrl, new ErrorSummary(taskContext.getErrorStatistics()));
    			else {
    				HashMap<String, Integer> source = new HashMap<String, Integer>();
    				source = taskContext.getErrorStatistics();
    				HashMap<String, Integer> merge = errorStatistics.get(driverUrl).getErrorCodeAndNum();
    				for(Map.Entry<String, Integer> entry : source.entrySet()){
    					if (!merge.containsKey(entry.getKey())){
    						merge.put(entry.getKey(), entry.getValue());
    					}
    					else{
    						Integer value = merge.get(entry.getKey()) + entry.getValue();
    						merge.put(entry.getKey(), value);
    					}
    				}
    				errorStatistics.put(driverUrl, new ErrorSummary(merge));
    			}
    		}
    	}
    }
    public void logErrorStatistics(Logger logger){
    	for (Map.Entry<String, ErrorSummary> driverEntry : errorStatistics.entrySet()){
    		for (Map.Entry<String, Integer> codeEntry : driverEntry.getValue().getErrorCodeAndNum().entrySet()){
    			logger.warn(driverEntry.getKey() + " : " + codeEntry.getKey() + " occured " + codeEntry.getValue() );
    		}
    	}
    }

	@Override
    public void disposeRuntime() {
        for (StageContext stage : stageRegistry)
            stage.disposeRuntime();
        config = null;
        future = null;
        currentStage = null;
        listeners = null;
    }
	

    public DriverRegistry getDriverRegistry() {
        return driverRegistry;
    }

    public void setDriverRegistry(DriverRegistry driverRegistry) {
        this.driverRegistry = driverRegistry;
    }
    
    @Override
    public DriverInfo[] getDriverInfos() {
        return driverRegistry.getAllDrivers();
    }

}
