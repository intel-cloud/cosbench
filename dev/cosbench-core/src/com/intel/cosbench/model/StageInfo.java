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

package com.intel.cosbench.model;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.intel.cosbench.bench.*;
import com.intel.cosbench.config.Stage;
import com.intel.cosbench.utils.ListRegistry;

public interface StageInfo extends LifeCycle, Benchmark {

    public String getId();

    public StageState getState();
    
    public void setState(String state, Date date);
    
    public void setState(StageState state, boolean archived);

    public StateInfo[] getStateHistory();

    public Stage getStage();

    public int getWorkCount();

    public int getWorkerCount();

    public Set<String> getOperations();

    public int getInterval();

    public int getTaskCount();

    public TaskInfo[] getTaskInfos();

    public Snapshot[] getSnapshots();
    
    public int getSnapshotCount();

	public void setReport(Report report);
	
	public ListRegistry<Snapshot> getSnapshotRegistry();
	
    public List<TaskReport> getTaskReports();

	public void setTaskReports(List<TaskReport> taskReports);

}
