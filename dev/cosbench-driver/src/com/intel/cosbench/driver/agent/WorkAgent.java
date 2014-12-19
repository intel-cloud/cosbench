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

import static com.intel.cosbench.bench.Mark.*;

import com.intel.cosbench.bench.ErrorStatistics;

import java.util.*;

import javax.naming.AuthenticationException;

import com.intel.cosbench.api.auth.AuthBadException;
import com.intel.cosbench.api.auth.AuthException;
import com.intel.cosbench.api.context.AuthContext;
import com.intel.cosbench.api.storage.StorageAPI;
import com.intel.cosbench.bench.*;
import com.intel.cosbench.config.Mission;
import com.intel.cosbench.driver.model.*;
import com.intel.cosbench.driver.operator.*;
import com.intel.cosbench.driver.util.AuthCachePool;
import com.intel.cosbench.driver.util.OperationPicker;
import com.intel.cosbench.log.Logger;
import com.intel.cosbench.service.AbortedException;

class WorkAgent extends AbstractAgent implements Session, OperationListener {

    private long start; /* agent startup time */
    private long begin; /* effective workload startup time */
    private long end; /* effective workload shut-down time */
    private long timeout; /* expected agent stop time */

    private long lop; /* last operation performed */
    private long lbegin; /* last sample emitted */
    private long lsample; /* last sample collected */
    private long lrsample; /* last sample collected during runtime */
    private long frsample; /* first sample emitted during runtime */

    private long curr; /* current time */
    private long lcheck; /* last check point time */
    private long check; /* next check point time */
    private long interval; /* interval between check points */

    private int totalOps; /* total operations to be performed */
//    private int op_count;
    private long totalBytes; /* total bytes to be transferred */
    private boolean has_histo; /* collect response time histogram data or not */

    private OperationPicker operationPicker;
    private OperatorRegistry operatorRegistry;

//    private boolean isFinished = false;
    private WatchDog dog = new WatchDog();

    private Status currMarks = new Status(); /* for snapshots */
    private Status globalMarks = new Status(); /* for the final report */

    public WorkAgent() {
        /* empty */
    }

    @Override
    public void setWorkerContext(WorkerContext workerContext) {
        super.setWorkerContext(workerContext);
        this.has_histo = workerContext.getMission().hasHisto();
        
        dog.setWorkerContext(workerContext);
    }

    public void setOperationPicker(OperationPicker operationPicker) {
        this.operationPicker = operationPicker;
    }

    public void setOperatorRegistry(OperatorRegistry operatorRegistry) {
        this.operatorRegistry = operatorRegistry;
    }

    @Override
    public int getIndex() {
        return workerContext.getIndex();
    }

    @Override
    public int getTotalWorkers() {
        return workerContext.getMission().getTotalWorkers();
    }

    @Override
    public Random getRandom() {
        return workerContext.getRandom();
    }

    @Override
    public StorageAPI getApi() {
        return workerContext.getStorageApi();
    }

    @Override
    public Logger getLogger() {
        return workerContext.getLogger();
    }
    
    public ErrorStatistics getErrorStatistics(){
    	return workerContext.getErrorStatistics();
    }

    @Override
    public OperationListener getListener() {
        return this;
    }

    @Override
    protected void execute() {
        initTimes();
        initLimites();
        initMarks();
        dog.watch(timeout);
        try {
            doWork(); // launch work
        } finally {
            dog.dismiss();
        }
        /* work agent has completed execution successfully */
    }

    private void initTimes() {
        Mission mission = workerContext.getMission();
        interval = mission.getInterval();
        lcheck = curr = start = System.currentTimeMillis();
        check = lcheck + interval * 1000;
        begin = start;
        timeout = 0L;
        lop = lrsample = lsample = start;
        frsample = lbegin = end = Long.MAX_VALUE;
    }

    private void initLimites() {
        Mission mission = workerContext.getMission();
        totalOps = mission.getTotalOps() / mission.getTotalWorkers();
        totalBytes = mission.getTotalBytes() / mission.getTotalWorkers();
        if (mission.getRuntime() == 0)
            return;
        begin = start + mission.getRampup() * 1000;
        end = begin + mission.getRuntime() * 1000;
        timeout = end + mission.getRampdown() * 1000;
    }

    private void initMarks() {
        Set<String> types = new LinkedHashSet<String>();
        for (OperatorContext op : operatorRegistry)
            types.add(getMarkType(op.getId(), op.getOpType(), op.getSampleType(), op.getName()));
        for (String type : types)
            currMarks.addMark(newMark(type));
        for (String type : types)
            globalMarks.addMark(newMark(type));
    }

    private void doWork() {
        doSnapshot();
        while (!workerContext.isFinished())
            try {
                performOperation();
			}catch (AbortedException ae) {
                if (lrsample > frsample)
                    doSummary();
                workerContext.setFinished(true);
            }
        doSnapshot();
    }
        

    private void performOperation() {
    	if(workerContext.getAuthApi() == null || workerContext.getStorageApi() == null) 
    		throw new AbortedException();
    	if(! workerContext.getStorageApi().isAuthValid())
    		reLogin();
        lbegin = System.currentTimeMillis();
        Random random = workerContext.getRandom();
        String op = operationPicker.pickOperation(random);
        OperatorContext context = operatorRegistry.getOperator(op);
        try{
        	context.getOperator().operate(this);
        }catch(AuthException ae) {
        	reLogin();
        }
    }
    
    @Override
    public void onSampleCreated(Sample sample) {
        curr = sample.getTimestamp().getTime();
		String type = getMarkType(sample.getOpId(), sample.getOpType(),
				sample.getSampleType(), sample.getOpName());
        currMarks.getMark(type).addSample(sample);
        if (lbegin >= begin && lbegin < end && curr > begin && curr <= end) {
            globalMarks.getMark(type).addSample(sample);
            operatorRegistry.getOperator(sample.getOpId()).addSample(sample);
            if (lbegin < frsample)
                frsample = lbegin; // first sample emitted during runtime
            lrsample = curr; // last sample collected during runtime
        }
        lsample = curr; // last sample collected
        trySnapshot(); // make a snapshot if necessary
    }

    private void trySnapshot() {
        if (lsample < check)
            return;
        doSnapshot();
        lcheck = System.currentTimeMillis();
        check = lcheck + interval * 1000;
    }

    private void doSnapshot() {
        long window = lsample - lcheck;
        Report report = new Report();
        for (Mark mark : currMarks) {
            report.addMetrics(Metrics.convert(mark, window));
            mark.clear();
        }
        Snapshot snapshot = new Snapshot(report);
        workerContext.setSnapshot(snapshot);
    }

    @Override
    public void onOperationCompleted(Result result) {
        curr = result.getTimestamp().getTime();
/* */
		String type = getMarkType(result.getOpId(), result.getOpType(),
				result.getSampleType(), result.getOpName());
        currMarks.getMark(type).addOperation(result);
        if (lop >= begin && lop < end && curr > begin && curr <= end)
            globalMarks.getMark(type).addOperation(result);
/* */
        lop = curr; // last operation performed
        trySummary(); // make a summary report if necessary
    }

    private void trySummary() {
        if ((timeout <= 0 || curr < timeout) // timeout
                && (totalOps <= 0 || getTotalOps() < totalOps) // operations
                && (totalBytes <= 0 || getTotalBytes() < totalBytes)) // bytes
            return; // not finished
        doSummary();
        
        workerContext.setFinished(true);
    }

    private void doSummary() {
/* */
        long window = lrsample - frsample;
        Report report = new Report();
        for (Mark mark : globalMarks)
            report.addMetrics(Metrics.convert(mark, window));
        workerContext.setReport(report);
/* */
    }

    private int getTotalOps() {
//    	return ++op_count;
    	
        int sum = 0;
        for (Mark mark : globalMarks)
            sum += mark.getTotalOpCount();
        return sum;
    }

    private long getTotalBytes() {
        long bytes = 0;
        for (Mark mark : globalMarks)
            bytes += mark.getByteCount();
        return bytes;
    }
    public void reLogin() {
    	LOGGER.debug("WorkAgent {} auth failed, now relogin",workerContext.getIndex());
		AuthContext authContext = workerContext.getStorageApi().getAuthContext();
		synchronized (AuthCachePool.getInstance()) {
			AuthCachePool.getInstance().remove(authContext.getID());
		}
    	try{
    		workerContext.getAuthApi().init();
    		authContext = workerContext.getAuthApi().login();
    		workerContext.getStorageApi().setAuthContext(authContext);
    		synchronized (AuthCachePool.getInstance()) {
				AuthCachePool.getInstance().put(authContext.getID(), authContext);
			}
    		LOGGER.debug("WorkAgent {} relogin successfully",workerContext.getIndex());
    	}catch(AuthException ae) {
    		workerContext.getAuthApi().dispose();
    		LOGGER.error("agent "+workerContext.getIndex()+" failed to login",ae);
    		throw new AgentException();
    	}	
    }

}
