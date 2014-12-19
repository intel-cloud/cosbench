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

package com.intel.cosbench.config;

import java.util.*;

import org.apache.commons.lang.StringUtils;

import com.intel.cosbench.config.common.ConfigUtils;
import com.intel.cosbench.config.common.KVConfigParser;

/**
 * The class encapsulates the mission delivering to driver.
 * 
 * @author ywang19, qzheng7
 *
 */
public class Mission implements Iterable<Operation> {

    private static final Auth DEFAULT_AUTH = new Auth("none");
    private static final Storage DEFAULT_STORAGE = new Storage("none");

    private String name;
    private int workers;
    private int offset = 0;
    private int interval = 5;
    private String division = "none";
    private int runtime = 0;
    private int rampup = 0;
    private int rampdown = 0;
    private int totalOps = 0;
    private long totalBytes = 0;
    private int totalWorkers;
    private String config = "";
    private Auth auth = DEFAULT_AUTH;
    private Storage storage = DEFAULT_STORAGE;
    private List<Operation> operations;
    
    

    public Mission() {
        /* empty */
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (StringUtils.isEmpty(name))
            throw new ConfigException("mission name cannot be empty");
        this.name = name;
    }

    public int getWorkers() {
        return workers;
    }

    public void setWorkers(int workers) {
        if (workers < 0)
            throw new ConfigException("illegal workers: " + workers);
        if (workers == 0)
            throw new ConfigException("must specify 'workers' for a mission");
        this.workers = workers;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        if (offset < 0)
            throw new ConfigException("illegal offset: " + offset);
        this.offset = offset;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        if (interval <= 0)
            throw new ConfigException("illegal interval: " + interval);
        this.interval = interval;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        if (StringUtils.isEmpty(division))
            throw new ConfigException("mission must have a default division");
        this.division = division;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        if (runtime < 0)
            /* runtime can be set to zero */
            throw new ConfigException("illegal runtime: " + runtime);
        this.runtime = runtime;
    }

    public int getRampup() {
        return rampup;
    }

    public void setRampup(int rampup) {
        if (rampup < 0)
            /* ramp up time can be set to zero */
            throw new ConfigException("illegal ramp up time: " + rampup);
        this.rampup = rampup;
    }

    public int getRampdown() {
        return rampdown;
    }

    public void setRampdown(int rampdown) {
        if (rampdown < 0)
            /* ramp up time can be set to zero */
            throw new ConfigException("illegal ramp down time: " + rampdown);
        this.rampdown = rampdown;
    }

    public int getTotalOps() {
        return totalOps;
    }

    public void setTotalOps(int totalOps) {
        if (totalOps < 0)
            /* total operations can be set to zero */
            throw new ConfigException("illegal total ops: " + totalOps);
        this.totalOps = totalOps;
    }

    public long getTotalBytes() {
        return totalBytes;
    }

    public void setTotalBytes(long totalBytes) {
        if (totalBytes < 0)
            /* total bytes can be set to zero */
            throw new ConfigException("illegal total bytes: " + totalBytes);
        this.totalBytes = totalBytes;
    }

    public int getTotalWorkers() {
        return totalWorkers;
    }

    public void setTotalWorkers(int totalWorkers) {
        if (totalWorkers < 0)
            throw new ConfigException("illegal total workers: " + totalWorkers);
        if (totalWorkers == 0)
            throw new ConfigException(
                    "must specify 'totalWorkers' for a mission");
        this.totalWorkers = totalWorkers;
    }
    
    public boolean hasHisto() {
    	if(config != null) {
    		return KVConfigParser.parse(config).getBoolean("histo", true);
    	}
    	
    	return true;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        /* configuration might be empty */
        this.config = config;
    }
    
    public Auth getAuth() {
        return auth;
    }

    public void setAuth(Auth auth) {
        if (auth == null)
            throw new ConfigException("a mission must have its auth");
        this.auth = auth;
    }

    public Storage getStorage() {
        return storage;
    }

    public void setStorage(Storage storage) {
        if (storage == null)
            throw new ConfigException("a mission must have its storge");
        this.storage = storage;
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public void setOperations(List<Operation> operations) {
        if (operations == null || operations.isEmpty())
            throw new ConfigException("a mission must have opertations");
        for(Operation op: operations) {
        	op.setConfig(ConfigUtils.inherit(op.getConfig(), this.config));
        }
        this.operations = operations;
    }

    @Override
    public Iterator<Operation> iterator() {
        return operations.iterator();
    }

    public void validate() {
        setName(getName());
        setWorkers(getWorkers());
        setTotalWorkers(getTotalWorkers());
        if (totalWorkers < workers || offset + workers > totalWorkers)
            throw new ConfigException(
                    "conflicting workers, totalWorkers, and offset");
        if (runtime == 0 && totalOps == 0 && totalBytes == 0)
            throw new ConfigException(
                    "no mission limits detectd, either runtime, total ops or total bytes has been set");
        auth.validate();
        storage.validate();
        setOperations(getOperations());
        for (Operation op : operations)
            if (op.getDivision() == null)
                op.setDivision(division);
        for (Operation op : operations)
            op.validate();
        int sum = 0;
        for (Operation op : operations)
            sum += op.getRatio();
        if (sum != 100)
            throw new ConfigException("op ratio should sum to 100");
    }

}
