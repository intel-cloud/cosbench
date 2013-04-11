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

package com.intel.cosbench.driver.repository;

import java.util.*;

import com.intel.cosbench.driver.model.MissionContext;
import com.intel.cosbench.model.MissionState;

class SimpleMissionList implements MissionList {

    private int maxCapacity;

    private int capacity = 0;
    private Map<String, MissionContext> list;

    private List<MissionContext> toBeRemoved;

    public SimpleMissionList(int maxCapacity) {
        this.maxCapacity = maxCapacity;
        this.list = new LinkedHashMap<String, MissionContext>(maxCapacity);
        this.toBeRemoved = new ArrayList<MissionContext>();
    }

    @Override
    public int count() {
        return list.size();
    }

    @Override
    public MissionContext fetch(String id) {
        return list.get(id);
    }

    @Override
    public MissionContext[] add(MissionContext mission) {
        toBeRemoved.clear(); // begin transaction
        list.put(mission.getId(), mission);
        capacity += 1;
        shrinkListSize();
        MissionContext[] result = new MissionContext[toBeRemoved.size()];
        result = toBeRemoved.toArray(result);
        toBeRemoved.clear(); // end transaction
        return result;
    }

    private void shrinkListSize() {
        Iterator<MissionContext> iter = list.values().iterator();
        while (capacity > maxCapacity && iter.hasNext())
            tryRemoveMissions(iter);
    }

    private void tryRemoveMissions(Iterator<MissionContext> iter) {
        MissionContext mission = iter.next();
        if (!MissionState.isStopped(mission.getState()))
            return; // retain active mission
        iter.remove();
        capacity -= 1;
        toBeRemoved.add(mission); // removal has been registered
    }

    @Override
    public Collection<MissionContext> values() {
        return list.values();
    }

}
