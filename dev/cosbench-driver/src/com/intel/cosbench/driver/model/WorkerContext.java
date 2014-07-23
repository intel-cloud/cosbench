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

package com.intel.cosbench.driver.model;

import java.util.Random;

import org.apache.commons.lang.math.RandomUtils;

import com.intel.cosbench.api.auth.AuthAPI;
import com.intel.cosbench.api.storage.StorageAPI;
import com.intel.cosbench.bench.*;
import com.intel.cosbench.config.Mission;
import com.intel.cosbench.log.Logger;
import com.intel.cosbench.model.WorkerInfo;

/**
 * This class encapsulates worker related information.
 * 
 * @author ywang19, qzheng7
 * 
 */
public class WorkerContext implements WorkerInfo {

    private int index;
    private Mission mission;
    private transient Logger logger;
    private ErrorStatistics errorStatistics;
    private transient AuthAPI authApi;
    private transient StorageAPI storageApi;

    private volatile boolean error = false;
    private volatile boolean aborted = false;
    private volatile boolean finished = false;
    
    /* Each worker starts with an empty snapshot */
    private transient volatile Snapshot snapshot = new Snapshot();
    /* Each worker starts with an empty report */
    private volatile Report report = new Report();
    /* Each worker has its private random object so as to enhance performance */
    private transient Random random = new Random(RandomUtils.nextLong());
    /* Each worker has its private required version */
    private volatile int version = 0;
    private volatile int runlen = 0;

    
    public WorkerContext() {
        /* empty */
    }

    @Override
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Mission getMission() {
        return mission;
    }

    public void setMission(Mission mission) {
        this.mission = mission;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public AuthAPI getAuthApi() {
        return authApi;
    }

    public void setAuthApi(AuthAPI authApi) {
        this.authApi = authApi;
    }

    public StorageAPI getStorageApi() {
        return storageApi;
    }

    public void setStorageApi(StorageAPI storageApi) {
        this.storageApi = storageApi;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public boolean isAborted() {
        return aborted;
    }

    public void setAborted(boolean aborted) {
        this.aborted = aborted;
    }

    @Override
    public Snapshot getSnapshot() {
    	if(snapshot.getVersion() < version)
    	{
    		logger.debug("Worker[{}] : blank snapshot is generated.", index);
    		Snapshot blankSnapshot = new Snapshot();

    		blankSnapshot.setVersion(version);
    		blankSnapshot.setMinVersion(version);
    		blankSnapshot.setMaxVersion(version);
    		
    		version++;
    		runlen++;
    		
    		return blankSnapshot;
    	}
    
    	// align snapshot metrics to compensate the under-counting due to blank snapshots.
    	if(runlen > 0)
    	{
	    	Report report = snapshot.getReport();
	    	Metrics[] metrics = report.getAllMetrics();
	
	    	for(int i=0; i<metrics.length; i++)
	    	{
	    		logger.debug("Worker[{}] : ratio={}", index, runlen+1);
	    		metrics[i].setThroughput(metrics[i].getThroughput()*(runlen+1));
	    		metrics[i].setBandwidth(metrics[i].getBandwidth()*(runlen+1));
	    	}
	    	
	    	runlen = 0;
    	}
    	
    	version++;
    	
    	return snapshot;
    }

    public void setSnapshot(Snapshot snapshot) {
    	this.snapshot = snapshot;

    	this.snapshot.setVersion(version);
    	this.snapshot.setMinVersion(version);
    	this.snapshot.setMaxVersion(version);
    }

    @Override
    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public Random getRandom() {
        return random;
    }

    public boolean isFinished() {
    	return finished;
    }
    
    public void setFinished(boolean finished) {
    	this.finished = finished;
    }
    
    public ErrorStatistics getErrorStatistics() {
		return errorStatistics;
	}

	public void setErrorStatistics(ErrorStatistics errorStatistics) {
		this.errorStatistics = errorStatistics;
	}

	@Override
    public synchronized void disposeRuntime() {
    	if(authApi != null) {
	        authApi.dispose();
	        authApi = null;
    	}
    	if(storageApi != null) {
	        storageApi.dispose();
	        storageApi = null;
    	}
    	finished = true;
//        random = null;
//        snapshot = new Snapshot();
//        logger = null;
    }

}
