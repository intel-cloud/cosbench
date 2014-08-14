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

import com.intel.cosbench.bench.Benchmark;
import com.intel.cosbench.config.*;

public interface WorkloadInfo extends LifeCycle, Benchmark {

    public String getId();

    public Date getSubmitDate();

    public Date getStartDate();

    public Date getStopDate();

    public WorkloadState getState();

    public StateInfo[] getStateHistory();

    public XmlConfig getConfig();

    public Workload getWorkload();

    public String[] getAllOperations();

    public int getStageCount();

    public StageInfo getCurrentStage();

    public StageInfo getStageInfo(String id);

    public StageInfo[] getStageInfos();

    public int getSnapshotCount();

	public void setWorkload(Workload workload);
	
	public void setArchived(boolean archived);
	
	public boolean getArchived();
	
	public DriverInfo[] getDriverInfos();

}
