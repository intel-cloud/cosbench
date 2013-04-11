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

import org.apache.commons.lang.StringUtils;

import com.intel.cosbench.driver.model.*;
import com.intel.cosbench.log.*;
import com.intel.cosbench.model.MissionState;

/**
 * This class represents one in-memory repository to store all missions
 * information.
 * 
 * @author ywang19, qzheng7
 * 
 */
public class RAMMissionRepository implements MissionRepository, MissionListener {

    private static final int MAX_MISSION_DEFAULT = 100;

    private static final String MAX_MISSION_KEY = "cosbench.driver.history";

    private static final Logger LOGGER = LogFactory.getSystemLogger();

    private MissionList missions;

    public RAMMissionRepository() {
        MissionList missions = new SimpleMissionList(getMaxCapacity());
        this.missions = missions;
    }

    private int getMaxCapacity() {
        int maxCapacity = MAX_MISSION_DEFAULT;
        String config = System.getProperty(MAX_MISSION_KEY);
        if (!StringUtils.isEmpty(config))
            try {
                maxCapacity = Integer.parseInt(config);
            } catch (NumberFormatException e) {
            }
        LOGGER.debug("will hold {} missions in RAM", maxCapacity);
        return maxCapacity; // max number of missions held in RAM
    }

    @Override
    public synchronized int getSize() {
        return missions.count();
    }

    @Override
    public synchronized void saveMission(MissionContext mission) {
        mission.addListener(this);
        MissionContext[] removed = missions.add(mission);
        LOGGER.debug("mission {} has been saved in RAM", mission.getId());
        LOGGER.debug("{} missions have been removed from RAM", removed.length);
    }

    @Override
    public synchronized MissionContext getMission(String id) {
        return missions.fetch(id);
    }

    @Override
    public synchronized MissionContext[] getAllMissions() {
        int size = missions.count();
        return missions.values().toArray(new MissionContext[size]);
    }

    @Override
    public synchronized MissionContext[] getActiveMissions() {
        List<MissionContext> result = new ArrayList<MissionContext>();
        for (MissionContext mission : missions.values())
            if (!MissionState.isStopped(mission.getState()))
                result.add(mission);
        return result.toArray(new MissionContext[result.size()]);
    }

    @Override
    public synchronized MissionContext[] getInactiveMissions() {
        List<MissionContext> result = new ArrayList<MissionContext>();
        for (MissionContext mission : missions.values())
            if (MissionState.isStopped(mission.getState()))
                result.add(mission);
        return result.toArray(new MissionContext[result.size()]);
    }

    @Override
    public void missionStopped(MissionContext mission) {
        mission.disposeRuntime();
        LOGGER.debug("mission {} has been disposed", mission.getId());
    }

}
