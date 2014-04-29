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

import java.util.*;
import java.util.concurrent.*;

import com.intel.cosbench.bench.Metrics;
import com.intel.cosbench.config.Stage;
import com.intel.cosbench.config.Work;
import com.intel.cosbench.controller.model.*;
import com.intel.cosbench.controller.schedule.*;
import com.intel.cosbench.controller.tasklet.*;
import com.intel.cosbench.log.*;
import com.intel.cosbench.model.TaskState;
import com.intel.cosbench.service.CancelledException;

/**
 * This class encapsulates one control class to run stages.
 * 
 * @author ywang19, qzheng7
 * 
 */
class StageRunner implements StageCallable {

    private static final Logger LOGGER = LogFactory.getSystemLogger();

    private StageContext stageContext;
    private ControllerContext controllerContext;

    private ExecutorService executor;

    public StageRunner() {
        /* empty */
    }

    public StageContext getStageContext() {
        return stageContext;
    }

    public void setStageContext(StageContext stageContext) {
        this.stageContext = stageContext;
    }

    public void setControllerContext(ControllerContext controllerContext) {
        this.controllerContext = controllerContext;
    }

    public void dispose() {
        if (executor != null)
            executor.shutdown();
        executor = null;
    }

    public void init() {
        scheduleWorks();
        createTasks();
        createExecutor();
    }

    private void scheduleWorks() {
        WorkScheduler scheduler = null;
        Stage stage = stageContext.getStage();
        DriverRegistry registry = controllerContext.getDriverRegistry();
        scheduler = Schedulers.defaultScheduler(stage, registry);
        stageContext.setScheduleRegistry(scheduler.schedule());
    }

    private void createTasks() {
        TaskRegistry registry = new TaskRegistry();
        int index = 1;
        for (SchedulePlan plan : stageContext.getScheduleRegistry()) {
            String id = "t" + index++;
            registry.addTask(createTaskContext(id, plan));
        }
        stageContext.setTaskRegistry(registry);
    }

    private static TaskContext createTaskContext(String id, SchedulePlan plan) {
        TaskContext context = new TaskContext();
        context.setId(id);
        context.setSchedule(plan);
        context.setState(TaskState.CREATED);
        return context;
    }

    private void createExecutor() {
        int taskCount = stageContext.getTaskCount();
        executor = Executors.newFixedThreadPool(taskCount);
    }

    @Override
    public StageRunner call() {
        try {
            runStage();
        } catch (CancelledException ce) {
            cancelStage();
        } catch (StageException se) {
            terminateStage();
        } catch (Exception e) {
            LOGGER.error("unexpected exception", e);
            terminateStage();
        }
        return this;
    }

    private void runStage() {
        bootTasks();
        if (Thread.interrupted())
            throw new CancelledException();
        submitTasks();
        if (Thread.interrupted())
            throw new CancelledException();
        authTasks();
        if (Thread.interrupted())
            throw new CancelledException();
        launchTasks();
        if (Thread.interrupted())
            throw new CancelledException();
        queryTasks();
        if (Thread.interrupted())
            throw new CancelledException();
        closeTasks();
        
		TaskRegistry tasks = stageContext.getTaskRegistry();
		for (TaskContext task : tasks) {
			if (task.getState().equals(TaskState.FAILED)) {
				stageContext.setState(FAILED);
				return;
			}
		}
		
		if (!reachAFRGoal()) {
			stageContext.setState(FAILED);
			return;
		}
        stageContext.setState(COMPLETED);
    }
    
    private boolean reachAFRGoal() {
    	String id = stageContext.getId();
    	boolean bool = true;
		stageContext.setReport(stageContext.mergeReport());
		for (Work work : stageContext.getStage().getWorks()) {
			List<String> operationIDs = work.getOperationIDs();
			long sumSampleCount = 0;
			long sumTotalSampleCount = 0;
			for (Metrics metric : stageContext.getReport().getAllMetrics()) {
				if (operationIDs.contains(metric.getOpId())) {
					sumSampleCount +=
							metric.getSampleCount() > 0 ? metric.getSampleCount() : 0;
					sumTotalSampleCount +=
							metric.getTotalSampleCount() > 0 ? metric.getTotalSampleCount() : 0;
				}
			}
			LOGGER.info("acceptable failure ratio of work {} = {}", id+"-"+work.getName(), (double)work.getAfr() / 1000000);
			LOGGER.info("real failure ratio of work {} = {}", id+"-"+work.getName(), 
					sumTotalSampleCount > 0 ? (double)(sumTotalSampleCount - sumSampleCount) / sumTotalSampleCount : "N/A");
			if ((sumTotalSampleCount - sumSampleCount) > sumTotalSampleCount * work.getAfr() / 1000000) {
				LOGGER.info("fail to reach the goal of acceptable failure ratio in stage {} - work {}", id, work.getName());
				bool = false;
				continue;
			}
			LOGGER.info("successfully reach the goal of acceptable failure ratio in stage {} - work {}", id, work.getName());
		}
		return bool;
	}

    private void bootTasks() {
        String id = stageContext.getId();
        stageContext.setState(BOOTING);
        TaskRegistry tasks = stageContext.getTaskRegistry();
        List<Tasklet> tasklets = Tasklets.newBooters(tasks);
        executeTasklets(tasklets);
        LOGGER.info("successfully booted all tasks in stage {}", id);
    }

    private void submitTasks() {
        String id = stageContext.getId();
        stageContext.setState(SUBMITTING);
        TaskRegistry tasks = stageContext.getTaskRegistry();
        List<Tasklet> tasklets = Tasklets.newSubmitters(tasks);
        executeTasklets(tasklets);
        LOGGER.info("successfully submitted all tasks in stage {}", id);
    }

    private void authTasks() {
        String id = stageContext.getId();
        stageContext.setState(AUTHING);
        TaskRegistry tasks = stageContext.getTaskRegistry();
        List<Tasklet> tasklets = Tasklets.newAuthenticators(tasks);
        executeTasklets(tasklets);
        LOGGER.info("successfully authenticated all tasks in stage {}", id);
    }

    private void launchTasks() {
        String id = stageContext.getId();
        stageContext.setState(LAUNCHING);
        TaskRegistry tasks = stageContext.getTaskRegistry();
        List<Tasklet> tasklets = Tasklets.newLaunchers(tasks);
        executeTasklets(tasklets);
        updateStageInfo();
        synchronized (stageContext) {
            stageContext.notify(); // sent a signal to the stage checker
        }
        LOGGER.info("successfully launched all tasks in stage {}", id);
    }

    private void updateStageInfo() {
        int interval = Integer.MAX_VALUE;
        for (TaskContext taskContext : stageContext.getTaskRegistry()) {
            int i = taskContext.getInterval();
            interval = i < interval ? i : interval;
        }
        stageContext.setInterval(interval);
        stageContext.setState(RUNNING); // update state after setting interval
    }

    private void queryTasks() {
        String id = stageContext.getId();
        TaskRegistry tasks = stageContext.getTaskRegistry();
        List<Tasklet> tasklets = Tasklets.newQueriers(tasks);
        executeTasklets(tasklets);
        LOGGER.info("successfully queried all tasks in stage {}", id);
    }

    private void closeTasks() {
        String id = stageContext.getId();
        stageContext.setState(CLOSING);
        TaskRegistry tasks = stageContext.getTaskRegistry();
        List<Tasklet> tasklets = Tasklets.newClosers(tasks);
        executeTasklets(tasklets);
        LOGGER.info("successfully closed all tasks in stage {}", id);
    }

    private void terminateStage() {
        String id = stageContext.getId();
        LOGGER.info("begin to terminate stage {}", id);
        TaskRegistry tasks = stageContext.getTaskRegistry();
        List<Tasklet> tasklets = Tasklets.newAborters(tasks);
        executeTasklets(tasklets); // terminators never fail
        stageContext.setState(TERMINATED);
        synchronized (stageContext) {
            stageContext.notify(); // sent a signal to the stage checker
        }
        LOGGER.info("stage {} has been terminated", id);
    }

    private void cancelStage() {
        String id = stageContext.getId();
        LOGGER.info("begin to cancel stage {}", id);
        executor.shutdownNow();
        if (Thread.interrupted())
            LOGGER.warn("get cancelled when canceling stage");
        try {
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException ie) {
            LOGGER.warn("get cancelled when canceling stage");
        }
        if (!executor.isTerminated())
            LOGGER.warn("fail to cancel current tasklets for stage {}", id);
        /*
         * Consider the stage cancelled even if its tasks have not.
         */
        int taskCount = stageContext.getTaskCount();
        executor = Executors.newFixedThreadPool(taskCount);
        TaskRegistry tasks = stageContext.getTaskRegistry();
        List<Tasklet> tasklets = Tasklets.newAborters(tasks);
        executeTasklets(tasklets); // terminators never fail
        stageContext.setState(CANCELLED);
        executor.shutdownNow();
        LOGGER.info("stage {} has been cancelled", id);
    }

    private void executeTasklets(List<Tasklet> tasklets) {
        int num = tasklets.size();
        LOGGER.debug("begin to execute tasklets, {} in total", num);
        try {
            executor.invokeAll(tasklets);
        } catch (InterruptedException ie) {
            throw new CancelledException(); // stage cancelled
        }
        LOGGER.debug("all {} tasklets have finished execution", num);
        List<String> errIds = new ArrayList<String>();
        for (TaskContext task : stageContext.getTaskRegistry())
            if (task.getState().equals(TaskState.ERROR)
                    || task.getState().equals(TaskState.INTERRUPTED))
                errIds.add(task.getId());
        if (errIds.isEmpty())
            return; // all of the tasks are fine
        LOGGER.error("detected tasks {} have encountered errors", errIds);
        throw new StageException(); // mark termination
    }

}
