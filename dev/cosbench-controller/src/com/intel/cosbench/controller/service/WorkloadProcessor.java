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

import static com.intel.cosbench.model.WorkloadState.*;
import static java.util.concurrent.TimeUnit.*;

import java.util.*;
import java.util.concurrent.*;

import org.apache.commons.lang.StringUtils;

import com.intel.cosbench.config.*;
import com.intel.cosbench.config.castor.CastorConfigTools;
import com.intel.cosbench.controller.model.*;
import com.intel.cosbench.log.*;
import com.intel.cosbench.model.StageState;
import com.intel.cosbench.service.*;
import com.intel.cosbench.service.IllegalStateException;

/**
 * This class encapsulates workload processing logic.
 * 
 * @author ywang19, qzheng7
 * 
 */
class WorkloadProcessor {

    private static final Logger LOGGER = LogFactory.getSystemLogger();

    private WorkloadContext workloadContext;
    private ControllerContext controllerContext;

    private ExecutorService executor;
    private List<StageContext> queue;

    public WorkloadProcessor() {
        /* empty */
    }

    public WorkloadContext getWorkloadContext() {
        return workloadContext;
    }

    public void setWorkloadContext(WorkloadContext workloadContext) {
        this.workloadContext = workloadContext;
        /*should be set after controllerContext set*/
        this.workloadContext.setDriverRegistry(controllerContext.getDriverRegistry());
    }

    public void setControllerContext(ControllerContext controllerContext) {
        this.controllerContext = controllerContext;
    }

    public void init() {
        resolveWorklaod();
        createStages();
        createExecutor();
    }

    public void dispose() {
	    if (executor != null)
	        executor.shutdown();
	    executor = null;
	}

	private void resolveWorklaod() {
        XmlConfig config = workloadContext.getConfig();
        WorkloadResolver resolver = CastorConfigTools.getWorkloadResolver();
        workloadContext.setWorkload(resolver.toWorkload(config));
    }

    private void createStages() {
        StageRegistry registry = new StageRegistry();
        int index = 1;
        for (Stage stage : workloadContext.getWorkload().getWorkflow()) {
            String id = "s" + index++ + "-" + stage.getName();
            registry.addStage(createStageContext(id, stage));
        }
        workloadContext.setStageRegistry(registry);
    }

    private static StageContext createStageContext(String id, Stage stage) {
    	initStageOpId(stage);
        StageContext context = new StageContext();
        context.setId(id);
        context.setStage(stage);
        context.setState(StageState.WAITING);
        return context;
    }
    
    private static void initStageOpId(Stage stage) {
    	int index = 0;
		for (Work work : stage.getWorks()) {
			for (Operation op : work.getOperations())
				if("none".equals(op.getId()))
					op.setId("op" + String.valueOf(++index));
		}
    }

    private void createExecutor() {
        executor = Executors.newFixedThreadPool(2);
        StageRegistry registry = workloadContext.getStageRegistry();
        queue = new LinkedList<StageContext>(registry.getAllItems());
    }

    public void process() {
        /* for strong consistency: a lock should be employed here */
        if (!workloadContext.getState().equals(QUEUING))
            throw new IllegalStateException(
                    "workload should be in the state of queuing but " + workloadContext.getState().name());
        String id = workloadContext.getId();
        LOGGER.info("begin to process workload {}", id);
        try {
            processWorkload();
        } catch (CancelledException ce) {
            cancelWorkload();
            return;
        } catch (WorkloadException we) {
            terminateWorkload();
            return;
		} catch (InterruptedException e) {
			terminateWorkload();
			return;
		} catch (Exception e) {
            LOGGER.error("unexpected exception", e);
            terminateWorkload();
            return;
        }
        workloadContext.logErrorStatistics(LOGGER);
        LOGGER.info("sucessfully processed workload {}", id);
    }

    /*
     * There is a small window when the workload is 'PROCESSING' but there is no
     * 'current stage' set! However, this inconsistent window is left AS-IS for
     * performance consideration.
     */
    private void processWorkload() throws InterruptedException {
        workloadContext.setState(PROCESSING);
        workloadContext.setStartDate(new Date());
        Iterator<StageContext> iter = queue.iterator();
        String trigger = workloadContext.getWorkload().getTrigger();
        executeTrigger(trigger, true, workloadContext.getId());
        while (iter.hasNext()) {
            StageContext stageContext = iter.next();
            iter.remove();
            runStage(stageContext);
        }
        executeTrigger(trigger, false, workloadContext.getId());
        workloadContext.setStopDate(new Date());
        workloadContext.setCurrentStage(null);
        workloadContext.mergeErrorStatistics();
		for (StageContext stageContext : workloadContext.getStageRegistry()
				.getAllItems()) {
			if (stageContext.getState().equals(StageState.FAILED)) {
				workloadContext.setState(FAILED);
				return;
			}
		}
        workloadContext.setState(FINISHED);
    }

    private static String millisToHMS(long millis) {

        long hrs = MILLISECONDS.toHours(millis) % 24;
        long min = MILLISECONDS.toMinutes(millis) % 60;
        long sec = MILLISECONDS.toSeconds(millis) % 60;

        return hrs + ":" + min + "::" + sec;
    }

    private void runStage(StageContext stageContext) throws InterruptedException {
        String id = stageContext.getId();
        int closuredelay = stageContext.getStage().getClosuredelay();

        String stageName = stageContext.getStage().getName();
//        String work0Type = stageContext.getStage().getWorks().get(0).getType();

        LOGGER.info("begin to run stage {}", id);

        LOGGER.info("============================================");
        LOGGER.info("START WORK: {}", stageName);

        long startStamp = System.currentTimeMillis();

        workloadContext.setCurrentStage(stageContext);
        if (stageName.equals("delay") && closuredelay > 0) {
			executeDelay(stageContext, closuredelay);
		} else {
			executeStage(stageContext);

			long elapsedTime = System.currentTimeMillis() - startStamp;

			LOGGER.info("END WORK:   {}, Time elapsed: {}", stageName, millisToHMS(elapsedTime));
			LOGGER.info("============================================");
			if(closuredelay > 0)
				executeDelay(stageContext, closuredelay);
		}
		LOGGER.info("successfully ran stage {}", id);
	}
    
	private void executeDelay(StageContext stageContext, int closuredelay)
			throws InterruptedException {

		LOGGER.info("sleeping for " + closuredelay + " seconds...");
		stageContext.setState(StageState.SLEEPING);
		Thread.sleep(closuredelay * 1000);
		LOGGER.info("sleep complete.");
		stageContext.setState(StageState.COMPLETED);
	} 

    private void executeStage(StageContext stageContext) {
        StageRunner runner = createStageRunner(stageContext);
        StageChecker checker = createStageChecker(stageContext);
        StageCallable[] callables = new StageCallable[] { runner, checker };
        String wsId = workloadContext.getId()+stageContext.getId();
        String trigger = stageContext.getStage().getTrigger();
        executeTrigger(trigger, true, wsId);
        try {
            executor.invokeAll(Arrays.asList(callables));
        } catch (InterruptedException ie) {
        	executeTrigger(trigger, false, wsId);
            throw new CancelledException(); // workload cancelled
        }
        runner.dispose(); // early dispose runner
        executeTrigger(trigger, false, wsId);
        if (!stageContext.getState().equals(StageState.TERMINATED))
            return;
        String id = stageContext.getId();
        LOGGER.error("detected stage {} encountered error", id);
        throw new WorkloadException(); // mark termination
    }

    private StageRunner createStageRunner(StageContext stageContext) {
        StageRunner runner = new StageRunner();
        runner.setStageContext(stageContext);
        runner.setControllerContext(controllerContext);
        runner.init();
        return runner;
    }

    private StageChecker createStageChecker(StageContext stageContext) {
        StageChecker checker = new StageChecker();
        checker.setStageContext(stageContext);
        return checker;
    }

    private void terminateWorkload() {
        String id = workloadContext.getId();
        LOGGER.info("begin to terminate workload {}", id);
        for (StageContext stageContext : queue)
            stageContext.setState(StageState.ABORTED);
        executeTrigger(workloadContext.getWorkload().getTrigger(), false, workloadContext.getId());
        workloadContext.setStopDate(new Date());
        workloadContext.setState(TERMINATED);
        LOGGER.info("successfully terminated workload {}", id);
    }
    
    private void executeTrigger(String trigger, boolean isEnable, String wsId) {
    	if (trigger == null || trigger.isEmpty())
			return;
    	TriggerRunner runner = new TriggerRunner(workloadContext.getDriverRegistry());
		runner.runTrigger(isEnable, trigger, wsId);
	}

    public void cancel() {
        String id = workloadContext.getId();
        Future<?> future = workloadContext.getFuture();
        /* for strong consistency: a lock should be employed here */
        if (future != null) {
            if (future.isCancelled())
                return; // already cancelled
            if (future.cancel(true)) {
                if (workloadContext.getState().equals(QUEUING)) {
                    for (StageContext stageContext : queue)
                        stageContext.setState(StageState.CANCELLED);
                    workloadContext.setStopDate(new Date());
                    workloadContext.setState(CANCELLED); // cancel it directly
                    LOGGER.info("successfully cancelled workload {}", id);
                    return; // workload cancel before processing
                }
                return; // cancel request submitted
            }
        }
        if (isStopped(workloadContext.getState())) {
            LOGGER.warn("workload {} not aborted as it is already stopped", id);
            return; // do nothing -- it is already stopped
        }
        workloadContext.setStopDate(new Date());
        workloadContext.setState(CANCELLED); // cancel it directly
        LOGGER.info("successfully cancelled workload {}", id);
    }

    private void cancelWorkload() {
        String id = workloadContext.getId();
        LOGGER.info("begin to cancel workload {}", id);
        executor.shutdown();
        if (Thread.interrupted())
            LOGGER.warn("get cancelled when canceling workload {}", id);
        try {
        	if (!executor.awaitTermination(5, TimeUnit.SECONDS)
        			&& !executor.awaitTermination(5, TimeUnit.SECONDS))
				executor.shutdownNow();
            if (!awaitTermination(5) && !awaitTermination(10) && !awaitTermination(30))
            	LOGGER.warn("get cancelled when canceling workload {}", id);
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        if (!executor.isTerminated())
            LOGGER.warn("fail to cancel current stage for workload {}", id);
        /*
         * Consider the workload aborted even if its current stage has not.
         */
        for (StageContext stageContext : queue)
            stageContext.setState(StageState.CANCELLED);
        executeTrigger(workloadContext.getWorkload().getTrigger(), false, workloadContext.getId());
        workloadContext.setStopDate(new Date());
        workloadContext.setState(CANCELLED);
        LOGGER.info("successfully cancelled workload {}", id);
    }
    
    private boolean awaitTermination(int seconds) {
        try {
            if (!executor.isTerminated()) {
                LOGGER.info("wait {} seconds for workload to cancel ...", seconds);
                executor.awaitTermination(seconds, TimeUnit.SECONDS);
            }
        } catch (InterruptedException e) {
            LOGGER.debug("get cancelled when canceling workload");
        }
        return executor.isTerminated();
    }

}
