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

package com.intel.cosbench.controller.tasklet;

import static com.intel.cosbench.model.TaskState.*;

import com.intel.cosbench.controller.model.TaskContext;
import com.intel.cosbench.log.*;
import com.intel.cosbench.model.DriverInfo;
import com.intel.cosbench.service.CancelledException;

abstract class AbstractTasklet implements Tasklet {

    protected static final Logger LOGGER = LogFactory.getSystemLogger();

    protected TaskContext context;

    protected abstract void execute();

    public AbstractTasklet(TaskContext context) {
        this.context = context;
    }

    protected DriverInfo getDriver() {
        return context.getSchedule().getDriver();
    }

    @Override
    public Tasklet call() {
        String id = context.getId();
        try {
            execute();
            LOGGER.debug("tasklet {} exits normally", id);
        } catch (CancelledException ce) {
            context.setState(INTERRUPTED); // waiting for cancellation
            LOGGER.debug("tasklet {} has been interrupted", id);
        } catch (TaskletException te) {
            context.setState(ERROR); // waiting for termination
            LOGGER.debug("tasklet {} is going to be terminated", id);
        } catch (Exception e) {
            context.setState(ERROR); // waiting for termination
            LOGGER.error("unexpected exception", e);
            LOGGER.debug("tasklet {} is going to be terminated", id);
        }
        return this; /* okay -- done */
    }

}
