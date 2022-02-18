/**

Copyright 2013 Intel Corporation, All Rights Reserved.
Copyright 2019 OpenIO Corporation, All Rights Reserved.

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
package com.intel.cosbench.controller.service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.intel.cosbench.controller.model.DriverRegistry;
import com.intel.cosbench.controller.tasklet.Tasklet;
import com.intel.cosbench.controller.tasklet.Tasklets;
import com.intel.cosbench.log.LogFactory;
import com.intel.cosbench.log.Logger;

public class TriggerRunner {
    DriverRegistry registry;
    private ExecutorService executor;
    private static final Logger LOGGER = LogFactory.getSystemLogger();

    public TriggerRunner(DriverRegistry registry) {
        this.registry = registry;
        createExecutor();
    }

    public void runTrigger(boolean option, String trigger, String wsId) {
        List<Tasklet> tasklets = Tasklets.newTriggers(trigger, registry, option, wsId);
        executeTasklets(tasklets, option);
        dispose();
    }

    private void createExecutor() {
        int taskCount = registry.getSize();
        executor = Executors.newFixedThreadPool(taskCount);
    }

    public void dispose() {
        if (executor != null)
            executor.shutdown();
        executor = null;
    }

    private void executeTasklets(List<Tasklet> tasklets, boolean option) {
        int num = tasklets.size();
        LOGGER.debug("begin to execute {}-trigger tasklets, {} in total",
                option ? "enable" : "kill", num);
        try {
            executor.invokeAll(tasklets);
        } catch (InterruptedException e) {
            LOGGER.debug("{}-trigger tasklets have interrupted",
                    option ? "enable" : "kill", num);
            return; //no return is ok?
        }
        LOGGER.debug("all {} trigger tasklets have finished execution", num);
    }
}
