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

import java.io.File;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import com.intel.cosbench.config.XmlConfig;
import com.intel.cosbench.controller.archiver.*;
import com.intel.cosbench.controller.model.*;
import com.intel.cosbench.controller.repository.*;
import com.intel.cosbench.log.*;
import com.intel.cosbench.model.*;
import com.intel.cosbench.service.ControllerService;

/**
 * This class is the major service for controller.
 * 
 * @author ywang19, qzheng7
 * 
 */
class COSBControllerService implements ControllerService, WorkloadListener {

	private static final Logger LOGGER = LogFactory.getSystemLogger();

	private AtomicInteger count; /* workload id generator */

	private ControllerContext context;
	private Map<String, WorkloadProcessor> processors;

	private ExecutorService executor;
	private WorkloadArchiver archiver = new SimpleWorkloadArchiver();
	private WorkloadRepository memRepo = new RAMWorkloadRepository();

	public COSBControllerService() {
		/* empty */
	}

	public void setContext(ControllerContext context) {
		this.context = context;
	}

	public void init() {
		count = new AtomicInteger(archiver.getTotalWorkloads());
		processors = new HashMap<String, WorkloadProcessor>();
		processors = Collections.synchronizedMap(processors);
		int concurrency = context.getConcurrency();
		executor = Executors.newFixedThreadPool(concurrency);
	}

	@Override
	public String submit(XmlConfig config) {
		LOGGER.debug("[ CT ] - submitting workload ... ");
		WorkloadContext workload = createWorkloadContext(config);
		WorkloadProcessor processor = createProcessor(workload);
		workload.addListener(this);
		processors.put(workload.getId(), processor);
		memRepo.saveWorkload(workload);
		LOGGER.debug("[ CT ] - workload {} submitted", workload.getId());
		return workload.getId();
	}

	private WorkloadContext createWorkloadContext(XmlConfig config) {
		WorkloadContext context = new WorkloadContext();
		context.setId(generateWorkloadId());
		context.setSubmitDate(new Date());
		context.setConfig(config);
		context.setState(WorkloadState.QUEUING);
		return context;
	}

	private String generateWorkloadId() {
		return "w" + count.incrementAndGet();
	}

	private WorkloadProcessor createProcessor(WorkloadContext workload) {
		WorkloadProcessor processor = new WorkloadProcessor();
		processor.setControllerContext(context);
		processor.setWorkloadContext(workload);
		processor.init();
		return processor;
	}

	@Override
	public void fire(String id) {
		final WorkloadProcessor processor = processors.get(id);
		if (processor == null)
			throw new IllegalStateException();
		LOGGER.debug("[ CT ] - starting workload {} ...", id);
		/* for strong consistency: a lock should be employed here */
		if (processor.getWorkloadContext().getFuture() != null)
			throw new IllegalStateException();
		class ControllerThread implements Runnable {

			@Override
			public void run() {
				processor.process(); // errors are reflected in state
				processor.getWorkloadContext().setFuture(null);
			}

		}
		Future<?> future = executor.submit(new ControllerThread());
		processor.getWorkloadContext().setFuture(future);
		yieldExecution(200); // give workload processor a chance
		LOGGER.debug("[ CT ] - workload {} started", id);
	}

	@Override
	public void cancel(String id) {
		WorkloadProcessor processor = processors.get(id);
		if (processor == null)
			return; // already stopped
		LOGGER.debug("[ CT ] - canceling workload{} ...", id);
		processor.cancel();
		yieldExecution(500); // give workload processor a chance
		LOGGER.debug("[ CT ] - workload {} cancelled", id);
	}

	private static void yieldExecution(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			LOGGER.warn("get interrupted when performing yield");
			Thread.currentThread().interrupt(); // re-interrupt
		}
	}

	@Override
	public ControllerInfo getControllerInfo() {
		return context;
	}

	@Override
	public WorkloadContext getWorkloadInfo(String id) {
		return memRepo.getWorkload(id);
	}

	@Override
	public WorkloadContext[] getActiveWorkloads() {
		return memRepo.getActiveWorkloads();
	}

	@Override
	public WorkloadContext[] getHistoryWorkloads() {
		return memRepo.getInactiveWorkloads();
	}

	@Override
	public void workloadStarted(WorkloadContext workload) {
		/* empty */
	}

	@Override
	public void workloadStopped(WorkloadContext workload) {
		String id = workload.getId();
		WorkloadProcessor processor = processors.remove(id);
		processor.dispose();
		archiver.archive(workload);
		LOGGER.debug("processor for workload {} has been detached", id);
	}

	@Override
	public File getWorkloadLog(WorkloadInfo info) {
		return archiver.getWorkloadLog(info);
	}

	@Override
	public File getWorkloadConfig(WorkloadInfo info) {
		return archiver.getWorkloadConfig(info);
	}

}
