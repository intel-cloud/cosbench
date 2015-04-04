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

package com.intel.cosbench.driver.service;

import static com.intel.cosbench.model.MissionState.SUBMITTED;

import java.util.*;
import java.util.concurrent.*;

import org.apache.commons.lang.math.RandomUtils;

import com.intel.cosbench.api.auth.AuthAPIService;
import com.intel.cosbench.api.storage.StorageAPIService;
import com.intel.cosbench.config.XmlConfig;
import com.intel.cosbench.driver.model.*;
import com.intel.cosbench.driver.repository.*;
import com.intel.cosbench.log.*;
import com.intel.cosbench.model.*;
import com.intel.cosbench.service.*;
import com.intel.cosbench.service.IllegalStateException;

/**
 * This class is the major service for driver.
 * 
 * @author ywang19, qzheng7
 * 
 */
class COSBDriverService implements DriverService, MissionListener {

    private static final Logger LOGGER = LogFactory.getSystemLogger();
    
    private DriverContext context;
    private Map<String, MissionHandler> handlers;
    
    private AuthAPIService authAPIs;
    private StorageAPIService storageAPIs;

    private ExecutorService executor;
    private MissionRepository memRepo = new RAMMissionRepository();
    
    public COSBDriverService() {
        /* empty */
    }

    public void setContext(DriverContext context) {
        this.context = context;
    }

    public void setAuthAPIs(AuthAPIService authAPIs) {
        this.authAPIs = authAPIs;
    }

    public void setStorageAPIs(StorageAPIService storageAPIs) {
        this.storageAPIs = storageAPIs;
    }

    public void init() {
        handlers = new HashMap<String, MissionHandler>();
        handlers = Collections.synchronizedMap(handlers);
        executor = Executors.newCachedThreadPool();
    }
    
    @Override
    public synchronized String submit(XmlConfig config) {
        LOGGER.debug("submitting mission ... ");
        
        MissionContext mission = createMissionContext(config);
        MissionHandler handler = createHandler(mission);
        mission.addListener(this);
        handlers.put(mission.getId(), handler);
        memRepo.saveMission(mission);
        LOGGER.debug("mission {} has been submitted", mission.getId());
        return mission.getId();
    }

    private static MissionContext createMissionContext(XmlConfig config) {
        MissionContext context = new MissionContext();
        context.setId(generateMissionId());
        context.setDate(new Date());
        context.setConfig(config);
        context.setState(SUBMITTED);
        return context;
    }

    private static String generateMissionId() {
        String id = "M" + Integer.toHexString(RandomUtils.nextInt(16));
        id += Integer.toHexString(new Date().hashCode());
        id += Integer.toHexString(RandomUtils.nextInt(16));
        return id.toUpperCase();
    }

    private MissionHandler createHandler(MissionContext mission) {
        MissionHandler handler = new MissionHandler();
        handler.setMissionContext(mission);
        handler.setAuthAPIs(authAPIs);
        handler.setStorageAPIs(storageAPIs);
        handler.init(); // configurations will be parsed
        return handler;
    }

    @Override
    public void login(String id) {
        final MissionHandler handler = handlers.get(id);
        LOGGER.info("handler=" + id);
        if (handler == null)
            throw new IllegalStateException("no mission handler");
        /* for strong consistency: a lock should be employed here */
        if (handler.getMissionContext().getFuture() != null)
            throw new IllegalStateException("mission is busy");
        class AuthThread implements Runnable {

            @Override
            public void run() {
                handler.login(); // errors are reflected in state
                handler.getMissionContext().setFuture(null);
            }

        }
        LOGGER.debug("authing mission {} ...", id);
        Future<?> future = null;
        synchronized(handler) {
        	future = executor.submit(new AuthThread());
        	handler.getMissionContext().setFuture(future);
        }
        LOGGER.debug("mission {} has been requested to auth", id);
        yieldExecution(200); // give mission handler a chance
        awaitTermination(future); // mission may be terminated or aborted
    }

    @Override
    public void launch(String id) {
        final MissionHandler handler = handlers.get(id);
        if (handler == null)
            throw new IllegalStateException("no mission handler");
        if (handler.getMissionContext().getFuture() != null)
            throw new IllegalStateException("mission is busy");
        class DriverThread implements Runnable {

            @Override
            public void run() {
                handler.stress(); // errors are reflected in state
                handler.getMissionContext().setFuture(null);
            }

        }
        LOGGER.debug("launching mission {} ...", id);
        Future<?> future = null;
        synchronized(handler) {
        	future = executor.submit(new DriverThread());
            handler.getMissionContext().setFuture(future);
        }
            
        LOGGER.debug("mission {} has been requested to launch", id);
        yieldExecution(200); // give mission handler a chance
    }

    @Override
    public void close(String id) {
        MissionHandler handler = handlers.get(id);
        if (handler == null)
            throw new IllegalStateException("no mission handler");
        LOGGER.debug("closing mission {} ... ", id);
        handler.close();
        LOGGER.debug("mission {} has been closed", id);
    }

    @Override
    public void abort(String id) {
        MissionHandler handler = handlers.get(id);
        if (handler == null)
            return; // already stopped
        LOGGER.debug("aborting mission {} ... ", id);
        handler.abort();
        yieldExecution(200); // give mission handler a chance
        LOGGER.debug("mission {} has been requested to abort", id);
    }

    private static void yieldExecution(int time) {
        try {
            Thread.sleep(time); // yield execution
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // re-interrupt
        }
    }

    private static void awaitTermination(Future<?> future) {
        try {
        	if(future != null)
        		future.get(); // wait forever
        } catch (CancellationException ce) {
            LOGGER.warn("task has been cancelled!", ce);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt(); // re-interrupt
        } catch (Exception ee) {
            LOGGER.error("unexcepted exception", ee);
            throw new UnexpectedException(ee); // stop execution
        }
    }

    @Override
    public DriverInfo getDriverInfo() {
        return context;
    }

    @Override
    public MissionInfo getMissionInfo(String id) {
        return memRepo.getMission(id);
    }

    @Override
    public MissionInfo[] getActiveMissions() {
        return memRepo.getActiveMissions();
    }

    @Override
    public MissionInfo[] getHistoryMissions() {
        return memRepo.getInactiveMissions();
    }

    @Override
    public void missionStopped(MissionContext mission) {
        String id = mission.getId();
        MissionHandler handler = handlers.remove(id);
        handler.dispose();
        LOGGER.debug("handler for mission {} has been detached", id);
    }

}
