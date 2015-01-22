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

import com.intel.cosbench.bench.*;
import com.intel.cosbench.config.*;
import com.intel.cosbench.model.*;
import com.intel.cosbench.utils.ListRegistry;

/**
 * This class encapsulates one work stage.
 * 
 * @author ywang19, qzheng7
 * 
 */
public class StageContext implements StageInfo {

    private String id;
    private volatile StageState state;
    private StateRegistry stateHistory = new StateRegistry();
    private Stage stage;

    private int interval;
    private transient ScheduleRegistry scheduleRegistry;
    private TaskRegistry taskRegistry;
    private SnapshotRegistry snapshotRegistry = new SnapshotRegistry();

    /* Report will be available after the stage is completed */
    private volatile Report report = null; // will be merged from task reports

    private transient List<StageListener> listeners = new ArrayList<StageListener>();
    
    private List<TaskReport> taskReports = new ArrayList<TaskReport>();

    public List<TaskReport> getTaskReports() {
		return taskReports;
	}

	public void setTaskReports(List<TaskReport> taskReports) {
		this.taskReports = taskReports;
	}

	public StageContext() {
        /* empty */
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public StageState getState() {
        return state;
    }

    public void setState(StageState state) {
    	setState(state, false);
    }
    
    public void setState(StageState state, boolean archived) {
        this.state = state;
        if(archived) 
        	return;
        stateHistory.addState(state.name());
        if (StageState.isStopped(state))
            fireStageStopped();
    }
    
    @Override
    public void setState(String state, Date date) {
    	stateHistory.addState(state,date);
    }

    private void fireStageStopped() {
        if (report == null)
            report = mergeReport();
        for (StageListener listener : listeners)
            listener.stageStopped(this);
    }

    public Report mergeReport() {
        if (taskRegistry == null)
            return new Report();
        ReportMerger merger = new ReportMerger();
        for (TaskContext task : taskRegistry){
        	TaskReport tReport=new TaskReport();
        	tReport.setReport(task.getReport());
        	tReport.setDriverName(task.getSchedule().getDriver().getName());
        	tReport.setDriverUrl(task.getSchedule().getDriver().getUrl());
        	taskReports.add(tReport);
        	merger.add(task.getReport());
        }
        return merger.merge();
    }

    @Override
    public StateInfo[] getStateHistory() {
        return stateHistory.getAllStates();
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public int getWorkCount() {
        return stage.getWorks().size();
    }

    @Override
    public int getWorkerCount() {
        int workers = 0;
        for (Work work : stage)
            workers += work.getWorkers();
        return workers;
    }

    @Override
    public Set<String> getOperations() {
        Set<String> ops = new LinkedHashSet<String>();
        for (Work work : stage)
            for (Operation op : work)
                ops.add(op.getType());
        return ops;
    }

    @Override
    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public ScheduleRegistry getScheduleRegistry() {
        return scheduleRegistry;
    }

    public void setScheduleRegistry(ScheduleRegistry scheduleRegistry) {
        this.scheduleRegistry = scheduleRegistry;
    }

    public TaskRegistry getTaskRegistry() {
        return taskRegistry;
    }

    @Override
    public int getTaskCount() {
        return taskRegistry == null ? 0 : taskRegistry.getSize();
    }

    @Override
    public TaskInfo[] getTaskInfos() {
        if (taskRegistry == null)
            return new TaskInfo[] {};
        return taskRegistry.getAllTasks();
    }

    public void setTaskRegistry(TaskRegistry taskRegistry) {
        this.taskRegistry = taskRegistry;
    }

    @Override
    public Snapshot getSnapshot() {
        if (taskRegistry == null)
            return new Snapshot();
        SnapshotMerger merger = new SnapshotMerger();
        for (TaskContext worker : taskRegistry)
            merger.add(worker.getSnapshot());
        return merger.merge();
    }

    @Override
    public Report getReport() {
        return report != null ? report : new Report();
    }

    @Override
    public void setReport(Report report) {
        this.report = report;
    }

    @Override
    public Snapshot[] getSnapshots() {
        return snapshotRegistry.getAllSnapshot();
    }

    @Override
    public int getSnapshotCount() {
        return snapshotRegistry.getSize();
    }

    public void makeSnapshot() {
        snapshotRegistry.addSnapshot(getSnapshot());
    }

    public void addListener(StageListener listener) {
        listeners.add(listener);
    }

    @Override
    public void disposeRuntime() {
        if (taskRegistry != null)
            for (TaskContext task : taskRegistry)
                task.disposeRuntime();
        scheduleRegistry = null;
        listeners = null;
    }

	@Override
	public ListRegistry<Snapshot> getSnapshotRegistry() {
		return snapshotRegistry;
	}

}
