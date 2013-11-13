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

package com.intel.cosbench.service;

import java.io.File;
import java.io.IOException;

import com.intel.cosbench.config.XmlConfig;
import com.intel.cosbench.model.*;

/**
 * The interface for controller service.
 * 
 * @author ywang19, qzheng7
 *
 */
public interface ControllerService {

    public String submit(XmlConfig config);
    
	public String resubmit(String id) throws IOException;

    public void fire(String id);
    
    public boolean changeOrder(String id, String neighId, boolean up);

    public void cancel(String id);

    public ControllerInfo getControllerInfo();

    public WorkloadInfo getWorkloadInfo(String id);

    public WorkloadInfo[] getActiveWorkloads();

    public WorkloadInfo[] getHistoryWorkloads();
    
    public WorkloadInfo[] getArchivedWorkloads();

    public File getWorkloadLog(WorkloadInfo info);

    public File getWorkloadConfig(WorkloadInfo info);
    
    public WorkloadLoader getWorkloadLoader();
    
    public boolean getloadArch();
    
    public void setloadArch(boolean loadArch);

}
