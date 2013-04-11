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

package com.intel.cosbench.controller.service;

import static com.intel.cosbench.model.StageState.*;

import com.intel.cosbench.controller.model.StageContext;
import com.intel.cosbench.log.*;
import com.intel.cosbench.service.CancelledException;

/**
 * This class encapsulates operations to check stage status.
 * 
 * @author ywang19, qzheng7
 * 
 */
public class StageChecker implements StageCallable {

    private static final Logger LOGGER = LogFactory.getSystemLogger();

    private StageContext stageContext;

    public StageChecker() {
        /* empty */
    }

    public void setStageContext(StageContext stageContext) {
        this.stageContext = stageContext;
    }

    @Override
    public StageChecker call() {
        try {
            checkStage();
        } catch (CancelledException ie) {
            /* do nothing */
        } catch (Exception e) {
            LOGGER.error("unexpected exception", e);
        }
        return this;
    }

    private void checkStage() {
        synchronized (stageContext) {
            try {
                if (!hasLaunched(stageContext.getState()))
                    stageContext.wait(); // wait for signal from the stage
                                         // runner
            } catch (InterruptedException e) {
                throw new CancelledException(); // stage cancelled
            }
        }
        if (isStopped(stageContext.getState()))
            return;
        hold(); // hold for 2.5 seconds
        do {
            sleep();
            stageContext.makeSnapshot();
            LOGGER.debug("made a snapshot for stage {}", stageContext.getId());
        } while (!isStopped(stageContext.getState()));
    }

    private void hold() {
        try {
            Thread.sleep(2500);
        } catch (InterruptedException ie) {
            throw new CancelledException(); // stage cancelled
        }
    }

    private void sleep() {
        long seconds = stageContext.getInterval();
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException ie) {
            throw new CancelledException(); // stage cancelled
        }
    }

}
