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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.StringUtils;

import com.intel.cosbench.config.XmlConfig;
import com.intel.cosbench.controller.archiver.*;
import com.intel.cosbench.controller.loader.SimpleWorkloadLoader;
import com.intel.cosbench.controller.model.*;
import com.intel.cosbench.controller.repository.*;
import com.intel.cosbench.log.*;
import com.intel.cosbench.model.*;
import com.intel.cosbench.service.ControllerService;
import com.intel.cosbench.service.WorkloadLoader;

/**
 * This class is the major service for controller.
 * 
 * @author ywang19, qzheng7
 * 
 */
class COSBControllerService implements ControllerService, WorkloadListener {

    private static final Logger LOGGER = LogFactory.getSystemLogger();

    private AtomicInteger count; /* workload id generator */
    
    private AtomicInteger order;

    private ControllerContext context;
    private Map<String, WorkloadProcessor> processors;
	private OrderThreadPoolExecutor executor;
    private WorkloadArchiver archiver = new SimpleWorkloadArchiver();
    private WorkloadLoader loader = new SimpleWorkloadLoader();
    private WorkloadRepository memRepo = new RAMWorkloadRepository();
    
    private boolean loadArch = false;

    public COSBControllerService() {
        /* empty */
    }

    public void setContext(ControllerContext context) {
        this.context = context;

        // ping drivers and set alive state
		Thread pingDriverThread = new Thread(
				new PingDriverRunner(context.getDriverInfos()));
		pingDriverThread.start();
    }

	public void init() {
		if(this.context == null) {
			LOGGER.error("Controller Context is not initialized.");
			System.exit(-1);
		}
		
		// initialize workload archiver and loader
		String archive_dir = context.getArchive_dir();
		archiver = new SimpleWorkloadArchiver(archive_dir);
	    loader = new SimpleWorkloadLoader(archive_dir);
				
        count = new AtomicInteger(archiver.getTotalWorkloads());
        order = new AtomicInteger(0);
        processors = new HashMap<String, WorkloadProcessor>();
        processors = Collections.synchronizedMap(processors);
        int concurrency = context.getConcurrency();
		executor = new OrderThreadPoolExecutor(concurrency, concurrency, 0L,
				TimeUnit.MILLISECONDS, new PriorityBlockingQueue<Runnable>(
						memRepo.getMaxCapacity(),
						new OrderFutureComparator()));

    }
	
	public void loadArchivedWorkload() throws IOException {
		List<WorkloadInfo> workloadContexts = loader.loadWorkloadRun();
		if (workloadContexts == null)
			return;
		for (WorkloadInfo workloadContext : workloadContexts)
			memRepo.saveWorkload((WorkloadContext) workloadContext);
	}
	
	public void unloadArchivedWorkload() {
		for(WorkloadContext workload : memRepo.getArchivedWorkloads()) {
			memRepo.removeWorkload(workload);
			workload = null;
		}
	}
	

    @Override
    public synchronized String submit(XmlConfig config) {
        LOGGER.debug("[ CT ] - submitting workload ... ");
        WorkloadContext workload = createWorkloadContext(config);
        WorkloadProcessor processor = createProcessor(workload);
        workload.addListener(this);
        processors.put(workload.getId(), processor);
        memRepo.saveWorkload(workload);
        LOGGER.debug("[ CT ] - workload {} submitted", workload.getId());
        return workload.getId();
    }
    
	@Override
	public String resubmit(String id) throws IOException {
		XmlConfig config = SimpleWorkloadLoader.getWorkloadConfg(memRepo
				.getWorkload(id));
		if (config != null)
			return submit(config);
		LOGGER.debug(
				"[ CT ] - workload {} resubmitted failed, has no workload config",
				id);
		throw new FileNotFoundException("configuration file for workload " + id);
	}

    private WorkloadContext createWorkloadContext(XmlConfig config) {
        WorkloadContext context = new WorkloadContext();
        context.setId(generateWorkloadId());
        context.setOrder(generateOrder());
        context.setSubmitDate(new Date());
        context.setConfig(config);
        context.setState(WorkloadState.QUEUING);
        return context;
    }
    
    public WorkloadLoader getWorkloadLoader() {
    	return loader;
    }

    public boolean getloadArch() {
    	return loadArch;
    }
    
    public void setloadArch(boolean loadArch) {
    	this.loadArch = loadArch;
    	
    	if(getloadArch()){
			try {
				loadArchivedWorkload();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	} else {
    		unloadArchivedWorkload();
    	}
    }
    
    private String generateWorkloadId() {
        return "w" + count.incrementAndGet();
    }
    
    private int generateOrder() {
    	return order.incrementAndGet();
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
    	if(id == null) {
    		throw new IllegalStateException("invalid workload id.");
    	}
        final WorkloadProcessor processor = processors.get(id);
        if (processor == null) {
            throw new IllegalStateException("workload processor is not initialized.");
        }
        LOGGER.debug("[ CT ] - starting workload {} ...", id);
        /* for strong consistency: a lock should be employed here */
		if (processor.getWorkloadContext().getFuture() != null)
			throw new IllegalStateException();
		ControllerThread ctrlThrd = new ControllerThread(processor);
		
		Future<?> future = null;
		synchronized(processor) {
			future = executor.submit(ctrlThrd);
			processor.getWorkloadContext().setFuture(future);
		}
        LOGGER.debug("[ CT ] - workload {} started", id);
		yieldExecution(200); // give workload processor a chance
    }
    
	@Override
	public boolean changeOrder(String id, String neighbourWId, boolean up) {
		if (StringUtils.isEmpty(neighbourWId)) {
			return changeOrder(id, up);
		}
		if(neighbourWId.equals(String.valueOf(0)))//multiple checked workload id
			return false;
		int order = processors.get(id).getWorkloadContext().getOrder();
		int neighOrder = processors.get(neighbourWId).getWorkloadContext()
				.getOrder();
		if (!up == order > neighOrder ? true : false)
			return false;

		if (processors.get(neighbourWId).getWorkloadContext().getState() != WorkloadState.QUEUING) {
			LOGGER.error(
					"[ CT ] - workload {} order failed cause it's highest order...",
					id);
			return false;
		}
		List<Integer> orders = new ArrayList<Integer>();
		Map<String, String> orderWorkloadMap = new HashMap<String, String>();
		for (WorkloadContext workload : getActiveWorkloads()) {
			if ((workload.getOrder() >= order && workload.getOrder() <= neighOrder)
					|| (workload.getOrder() <= order && workload
							.getOrder() >= neighOrder)) {
				orders.add(workload.getOrder());
				orderWorkloadMap.put(String.valueOf(workload.getOrder()),
						workload.getId());
			}
		}
		Integer[] orderArray = orders.toArray(new Integer[orders.size()]);
		Arrays.sort(orderArray);
		
		if (up) {
			for (int i = orderArray.length - 2; i >= 0; i--) {
				processors.get(orderWorkloadMap.get(String.valueOf(orderArray[i])))
						.getWorkloadContext().setOrder(orderArray[i + 1]);
			}
		} else {
			for (int i = 1; i <orderArray.length; i++) {
				processors.get(orderWorkloadMap.get(String.valueOf(orderArray[i])))
						.getWorkloadContext().setOrder(orderArray[i - 1]);
			}
		}
		processors.get(id).getWorkloadContext().setOrder(neighOrder);
		for (String workloadId : orderWorkloadMap.values()) {
			if (!processors.get(workloadId).getWorkloadContext().getFuture()
					.cancel(true)) {
				LOGGER.error(
						"[ CT ] - change workload {} order failed cause can't remove workload...",
						workloadId);
				return false;
			}
			processors.get(workloadId).getWorkloadContext().setFuture(null);
			fire(workloadId);
		}
		return true;
	}
	
	public boolean changeOrder(String id, boolean up) {
		int order = processors.get(id).getWorkloadContext().getOrder();
		int neighbourOrder = 0;
		List<Integer> orders = new ArrayList<Integer>();
		for(WorkloadContext workload:getActiveWorkloads()){
			orders.add(workload.getOrder());
		}
		Integer[] orderArray = orders.toArray(new Integer[orders.size()]);
		Arrays.sort(orderArray);
		if (up) {
			for (int i = orderArray.length - 1; i >= 0; i--) {
				if (orderArray[i] < order) {
					neighbourOrder = orderArray[i];
					break;
				}
			}
		}else{
			for (int i = 0; i < orderArray.length; i++) {
				if (orderArray[i] > order) {
					neighbourOrder = orderArray[i];
					break;
				}
			}
		}
		String neighbourWId = String.valueOf(0);
		for(WorkloadContext workload:getActiveWorkloads()){
			if (workload.getOrder() == neighbourOrder) {
				neighbourWId = workload.getId();
			}
		}
		if (neighbourWId.equals(String.valueOf(0)))//can't find neighbour workload
			return false;
		if (processors.get(neighbourWId).getWorkloadContext().getState() != WorkloadState.QUEUING) {
			LOGGER.debug(
					"[ CT ] - workload {} order failed cause it's highest order...",
					id);
			return false;
		}
		if (!processors.get(id).getWorkloadContext().getFuture().cancel(true)
				|| !processors.get(neighbourWId).getWorkloadContext()
						.getFuture().cancel(true)) {
			LOGGER.error(
					"[ CT ] - change workload {} {} order failed cause can't remove workload...",
					id, neighbourWId);
			return false;
		}
		processors.get(id).getWorkloadContext().setFuture(null);
		processors.get(neighbourWId).getWorkloadContext().setFuture(null);
		processors.get(id).getWorkloadContext().setOrder(neighbourOrder);
		processors.get(neighbourWId).getWorkloadContext().setOrder(order);
		fire(id);
		fire(neighbourWId);
		return true;
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
	public WorkloadInfo[] getArchivedWorkloads() {
		return memRepo.getArchivedWorkloads();
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
