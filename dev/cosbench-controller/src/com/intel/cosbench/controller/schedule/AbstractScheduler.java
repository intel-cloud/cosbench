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

import com.intel.cosbench.config.*;
import com.intel.cosbench.controller.model.*;

/**
 * The base class of scheduler.
 * 
 * @author ywang19, qzheng7
 * 
 */
abstract class AbstractScheduler implements WorkScheduler {

    protected List<Work> works = new ArrayList<Work>();

    protected Map<String, DriverContext> drivers = new LinkedHashMap<String, DriverContext>();

    public AbstractScheduler() {
        /* empty */
    }

    protected void init(Stage stage, DriverRegistry registry) {
        for (Work work : stage)
            works.add(work);
        for (DriverContext driver : registry)
            drivers.put(driver.getName(), driver);
    }

    protected static SchedulePlan createSchedule(Work work,
            DriverContext driver, int offset, int workers) {
        SchedulePlan plan = new SchedulePlan();
        plan.setWork(work);
        plan.setDriver(driver);
        plan.setOffset(offset);
        plan.setWorkers(workers);
        return plan;
    }

    protected static SchedulePlan createSchedule(Work work, DriverContext driver) {
        return createSchedule(work, driver, 0, work.getWorkers());
    }

}
