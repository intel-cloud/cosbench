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
import com.intel.cosbench.bench.ErrorStatistics;
import com.intel.cosbench.config.Mission;
import com.intel.cosbench.log.LogManager;

public interface MissionInfo extends LifeCycle, Benchmark {

    public String getId();

    public Date getDate();

    public MissionState getState();

    public StateInfo[] getStateHistory();

    public Mission getMission();

    public LogManager getLogManager();

    public int getWorkerCount();

    public WorkerInfo[] getWorkerInfos();
        
    public ErrorStatistics getErrorStatistics();

}
