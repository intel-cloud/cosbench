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

import java.util.*;

import com.intel.cosbench.controller.model.WorkloadContext;
import com.intel.cosbench.model.WorkloadState;

class SimpleWorkloadList implements WorkloadList {

    private int maxCapacity;

    private int capacity = 0;
    private Map<String, WorkloadContext> list;

    private List<WorkloadContext> toBeRemoved;

    public SimpleWorkloadList(int maxCapacity) {
        this.maxCapacity = maxCapacity;
        this.list = new LinkedHashMap<String, WorkloadContext>(maxCapacity);
        this.toBeRemoved = new ArrayList<WorkloadContext>();
    }

    @Override
    public int count() {
        return list.size();
    }

    @Override
    public WorkloadContext fetch(String id) {
        return list.get(id);
    }

    @Override
    public WorkloadContext[] add(WorkloadContext workload) {
        toBeRemoved.clear(); // begin transaction
        list.put(workload.getId(), workload);
        capacity = count(); // size of list	
        shrinkListSize();
        WorkloadContext[] result = new WorkloadContext[toBeRemoved.size()];
        result = toBeRemoved.toArray(result);
        toBeRemoved.clear(); // end transaction
        return result;
    }
    
    @Override
    public void remove(WorkloadContext workload) {
    	list.remove(workload.getId());
    }

    private void shrinkListSize() {
        Iterator<WorkloadContext> iter = list.values().iterator();
        while (capacity > maxCapacity && iter.hasNext())
            tryRemoveWorkloads(iter);
    }

    private void tryRemoveWorkloads(Iterator<WorkloadContext> iter) {
        WorkloadContext workload = iter.next();
        if (!WorkloadState.isStopped(workload.getState()))
            return; // retain active workload
        iter.remove();
        capacity -= 1;
        toBeRemoved.add(workload); // removal has been registered
    }

    @Override
    public Collection<WorkloadContext> values() {
        return list.values();
    }

}
