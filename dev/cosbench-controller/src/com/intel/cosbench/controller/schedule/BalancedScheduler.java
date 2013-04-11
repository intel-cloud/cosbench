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

package com.intel.cosbench.controller.schedule;

import java.util.*;

import org.apache.commons.lang.StringUtils;

import com.intel.cosbench.config.Work;
import com.intel.cosbench.controller.model.*;

/**
 * This class encapsulates one balanced scheduler, which tries best to evenly
 * distribute work to different driver.
 * 
 * @author ywang19, qzheng7
 * 
 */
class BalancedScheduler extends AbstractScheduler {

    private int allocIdx;
    private int[] allocMap;

    private ScheduleRegistry schedules = new ScheduleRegistry();

    public BalancedScheduler() {
        /* empty */
    }

    @Override
    public ScheduleRegistry schedule() {
        honorUserSchedules();
        scheduleRestWorks();
        return schedules;
    }

    private void honorUserSchedules() {
        Set<String> toRemove = new HashSet<String>();
        List<Work> unscheduled = new ArrayList<Work>();
        for (Work work : works) {
            DriverContext driver = fetchDriver(work.getDriver());
            if (driver == null) {
                unscheduled.add(work);
                continue;
            }
            toRemove.add(driver.getName());
            schedules.addSchedule(createSchedule(work, driver));
        }
        for (String driver : toRemove)
            drivers.remove(driver);
        works = unscheduled;
    }

    private void scheduleRestWorks() {
        if (works.size() == 0)
            return;
        if (drivers.size() == 0)
            throw new ScheduleException("no free driver available");
        allocIdx = 0;
        allocMap = new int[drivers.size()];
        for (Work work : works)
            doSchedule(work);
    }

    private void doSchedule(Work work) {
        int driverNum = allocMap.length;
        int base = work.getWorkers() / driverNum;
        int extra = work.getWorkers() % driverNum;

        for (int i = 0; i < driverNum; i++)
            allocMap[i] = base;
        int lower = allocIdx;
        int upper = lower + extra;
        for (int i = lower; i < upper; i++)
            allocMap[i % driverNum]++;
        allocIdx = upper % driverNum;

        int idx = 0;
        int offset = 0;
        int workers = 0;
        for (DriverContext driver : drivers.values()) {
            if ((workers = allocMap[idx++]) == 0)
                continue;
            schedules
                    .addSchedule(createSchedule(work, driver, offset, workers));
            offset += workers;
        }
    }

    private DriverContext fetchDriver(String name) {
        if (StringUtils.isEmpty(name))
            return null;
        if (StringUtils.equals(name, "none"))
            return null;
        return drivers.get(name);
    }

}
