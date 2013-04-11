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

package com.intel.cosbench.driver.agent;

import com.intel.cosbench.driver.model.WorkerContext;
import com.intel.cosbench.log.*;
import com.intel.cosbench.service.AbortedException;

abstract class AbstractAgent implements Agent {

    protected static final Logger LOGGER = LogFactory.getSystemLogger();

    protected WorkerContext workerContext;

    protected abstract void execute();

    public AbstractAgent() {
        /* empty */
    }

    public void setWorkerContext(WorkerContext workerContext) {
        this.workerContext = workerContext;
    }

    @Override
    public Agent call() throws Exception {
        int idx = workerContext.getIndex();
        try {
            execute();
            LOGGER.debug("agent {} exits normally", idx);
        } catch (AbortedException be) {
            workerContext.setAborted(true);
            LOGGER.debug("agent {} has been aborted", idx);
        } catch (AgentException ae) {
            workerContext.setError(true);
            LOGGER.debug("agent {} terminated with error", idx);
        } catch (Exception e) {
            workerContext.setError(true);
            LOGGER.error("unexpected exception", e);
            LOGGER.debug("agent {} terminated with error", idx);
        }
        return this;
    }

    protected Logger getMissionLogger() {
        return workerContext.getLogger();
    }

}
