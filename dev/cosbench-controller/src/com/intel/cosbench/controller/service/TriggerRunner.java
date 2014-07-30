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
