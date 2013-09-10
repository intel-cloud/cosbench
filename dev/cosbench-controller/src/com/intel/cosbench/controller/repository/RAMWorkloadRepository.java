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

package com.intel.cosbench.controller.repository;

import java.io.*;
import java.util.*;

import org.apache.commons.lang.StringUtils;

import com.intel.cosbench.controller.model.*;
import com.intel.cosbench.log.*;
import com.intel.cosbench.model.WorkloadState;

/**
 * This class represents one in-memory repository to store all workloads
 * information.
 * 
 * @author ywang19, qzheng7
 * 
 */
public class RAMWorkloadRepository implements WorkloadRepository,
        WorkloadListener {

    private static final int MAX_WORKLOAD_DEFAULT = 100;

    private static final String MAX_WORKLOAD_KEY = "cosbench.controller.history";

    private static final Logger LOGGER = LogFactory.getSystemLogger();

    private File stops; /* a file holding started workloads */
    private File starts; /* a file holding stopped workloads */

    private WorkloadList workloads;

    public RAMWorkloadRepository() {
        stops = new File("stop");
        starts = new File("start");
        WorkloadList workloads = new SimpleWorkloadList(getMaxCapacity());
        this.workloads = workloads;
    }
    
    @Override
    public int getMaxCapacity() {
        int maxCapacity = MAX_WORKLOAD_DEFAULT;
        String config = System.getProperty(MAX_WORKLOAD_KEY);
        if (!StringUtils.isEmpty(config))
            try {
                maxCapacity = Integer.parseInt(config);
            } catch (NumberFormatException e) {
            }
        LOGGER.debug("will hold {} workloads in RAM", maxCapacity);
        return maxCapacity; // max number of workloads held in RAM
    }

    @Override
    public synchronized int getSize() {
        return workloads.count();
    }

    @Override
    public synchronized void saveWorkload(WorkloadContext workload) {
        workload.addListener(this);
        WorkloadContext[] removed = workloads.add(workload);
        LOGGER.debug("workload {} has been saved in RAM", workload.getId());
        LOGGER.debug("{} workloads have been removed from RAM", removed.length);
    }
    
    @Override
    public synchronized void removeWorkload(WorkloadContext workload) {
    	workloads.remove(workload);
    } 

    @Override
    public synchronized WorkloadContext getWorkload(String id) {
        return workloads.fetch(id);
    }

    @Override
    public synchronized WorkloadContext[] getAllWorkloads() {
        int size = workloads.count();
        return workloads.values().toArray(new WorkloadContext[size]);
    }

    @Override
    public synchronized WorkloadContext[] getActiveWorkloads() {
        List<WorkloadContext> result = new ArrayList<WorkloadContext>();
        for (WorkloadContext workload : workloads.values())
            if (!WorkloadState.isStopped(workload.getState()))
                result.add(workload);
        return result.toArray(new WorkloadContext[result.size()]);
    }

    @Override
    public synchronized WorkloadContext[] getInactiveWorkloads() {
        List<WorkloadContext> result = new ArrayList<WorkloadContext>();
        for (WorkloadContext workload : workloads.values())
            if (WorkloadState.isStopped(workload.getState()) && !workload.getArchived())
                result.add(workload);
        return result.toArray(new WorkloadContext[result.size()]);
    }
    
    @Override
    public synchronized WorkloadContext[] getArchivedWorkloads() {
    	List<WorkloadContext> result = new ArrayList<WorkloadContext>();
        for (WorkloadContext workload : workloads.values())
            if (workload.getArchived())
                result.add(workload);
        return result.toArray(new WorkloadContext[result.size()]);
    }

    @Override
    public void workloadStarted(WorkloadContext workload) {
        markAsStarted(workload);
    }

    private void markAsStarted(WorkloadContext workload) {
        try {
            appendToStarts(workload);
        } catch (Exception e) {
            LOGGER.error("fail to mark workload", e);
        }
    }

    private void appendToStarts(WorkloadContext workload) throws IOException {
        String id = workload.getId();
        Writer writer = new BufferedWriter(new FileWriter(starts, true));
        try {
            writer.write(id);
            writer.write('\n');
            writer.flush();
        } finally {
            writer.close();
        }
        String path = starts.getAbsolutePath();
        LOGGER.debug("workload {} has been appened to {}", id, path);
    }

    @Override
    public void workloadStopped(WorkloadContext workload) {
        markAsStopped(workload);
        String id = workload.getId();
        workload.disposeRuntime();
        LOGGER.debug("runtime resources have been released for workload {}", id);
    }

    private void markAsStopped(WorkloadContext workload) {
        try {
            appendToStops(workload);
        } catch (Exception e) {
            LOGGER.error("fail to mark workload", e);
        }
    }

    private void appendToStops(WorkloadContext workload) throws IOException {
        String id = workload.getId();
        Writer writer = new BufferedWriter(new FileWriter(stops, true));
        try {
            writer.write(id);
            writer.write('\n');
            writer.flush();
        } finally {
            writer.close();
        }
        String path = stops.getAbsolutePath();
        LOGGER.debug("workload {} has been appened to {}", id, path);
    }

}
