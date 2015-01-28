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

import java.awt.List;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.client.HttpClient;
import org.codehaus.jackson.map.ObjectMapper;

import com.intel.cosbench.bench.*;
import com.intel.cosbench.model.*;

public class TaskContext implements TaskInfo {

    private String id;
    private volatile TaskState state;
    private SchedulePlan schedule;

    private transient HttpClient httpClient;
    private transient ObjectMapper mapper;

    private String missionId;
    /* The interval that is chosen by the driver */
    private int interval;
    /* Each task starts with an empty snapshot */
    private transient volatile Snapshot snapshot = new Snapshot();
    /* Each task starts with an empty report */
    private transient volatile Report report = new Report();
    /* Each task starts with an empty log */
    private volatile transient String log = "[N/A]";
 
    /* Each task starts with an empty error statistics log*/
    private volatile transient HashMap<String, Integer> errorStatistics = new HashMap<String, Integer>();
    /*Each task for workers report starts with empty*/
    private  ArrayList<Metrics> wrReport = new ArrayList<Metrics>();

	public ArrayList<Metrics> getWrReport() {
		return wrReport;
	}

	public void setWrReport(ArrayList<Metrics> wrReport) {
		this.wrReport = wrReport;
	}

	public TaskContext() {
        /* empty */
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TaskState getState() {
        return state;
    }

    public void setState(TaskState state) {
        this.state = state;
    }

    public SchedulePlan getSchedule() {
        return schedule;
    }

    public void setSchedule(SchedulePlan schedule) {
        this.schedule = schedule;
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public String getMissionId() {
        return missionId;
    }

    public void setMissionId(String missionId) {
        this.missionId = missionId;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public Snapshot getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(Snapshot snapshot) {
        this.snapshot = snapshot;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public String getLog() {
        return this.log;
    }

    public void setLog(String log) {
        this.log = log;
    }
   

    public HashMap<String, Integer> getErrorStatistics() {
		return errorStatistics;
	}

	public void setErrorStatistics(HashMap<String, Integer> errorStatistics) {
		this.errorStatistics = errorStatistics;
	}

	@Override
    public void disposeRuntime() {
    	if(TaskState.isStopped(state)) {
	        httpClient = null;
	        mapper = null;
	        report = null;
	        log = null;
	        snapshot = new Snapshot();
    	}
    }

}
