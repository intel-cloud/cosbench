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

package com.intel.cosbench.controller.model;

import com.intel.cosbench.config.Work;
import com.intel.cosbench.model.ScheduleInfo;

/**
 * The class represents one schedule plan, one schedule plan depends on a few
 * factors like the driver, work, total worker count, current worker number...
 * 
 * @author ywang19, qzheng7
 * 
 */
public class SchedulePlan implements ScheduleInfo {

    private Work work;
    private int offset;
    private int workers;
    private DriverContext driver;

    public SchedulePlan() {
        /* empty */
    }

    @Override
    public Work getWork() {
        return work;
    }

    public void setWork(Work work) {
        this.work = work;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public int getWorkers() {
        return workers;
    }

    public void setWorkers(int workers) {
        this.workers = workers;
    }

    @Override
    public DriverContext getDriver() {
        return driver;
    }

    public void setDriver(DriverContext driver) {
        this.driver = driver;
    }

}
