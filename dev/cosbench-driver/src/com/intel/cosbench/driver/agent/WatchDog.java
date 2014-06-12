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

import java.util.*;

import com.intel.cosbench.driver.model.WorkerContext;
import com.intel.cosbench.log.*;

class WatchDog extends TimerTask {

    private static final Logger LOGGER = LogFactory.getSystemLogger();

    private WorkerContext workerContext;

    private volatile boolean cancel = false;

    private Timer timer = new Timer(); /* alarm service */

    public WatchDog() {
        /* empty */
    }

    public void setWorkerContext(WorkerContext workerContext) {
        this.workerContext = workerContext;
    }

    public void dismiss() {
        timer.cancel();
        this.cancel = true;
        LOGGER.debug("timeout service has been dismissed");
    }

    public void watch(long timeout) {
        if (timeout != 0)
            timer.schedule(this, new Date(timeout + 1000));
    }

    @Override
    public void run() {
        int idx = workerContext.getIndex();
        int secs = 10;
        while (secs > 0 && !cancel) {
            LOGGER.debug("work agent {} will timeout in {} seconds", idx, secs);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ignore) {
            }
            secs -= 5;
        }
        if (!cancel) {
            workerContext.getStorageApi().abort();
        	workerContext.disposeRuntime();
        	
            LOGGER.debug("work agent {} has been alerted for timeout", idx);
        } else
            LOGGER.debug("work agent {} has completed before timeout", idx);
    }

}
