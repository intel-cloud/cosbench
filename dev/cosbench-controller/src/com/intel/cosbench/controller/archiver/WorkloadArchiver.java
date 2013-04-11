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

package com.intel.cosbench.controller.archiver;

import java.io.File;

import com.intel.cosbench.model.WorkloadInfo;

/**
 * The interface for workload archiver, which provides possibility to archive
 * data to different target like file system, database...
 * 
 * @author ywang19, qzheng7
 * 
 */
public interface WorkloadArchiver {

    public void archive(WorkloadInfo workload);

    public int getTotalWorkloads();

    public File getWorkloadLog(WorkloadInfo workload);

    public File getWorkloadConfig(WorkloadInfo workload);

}
