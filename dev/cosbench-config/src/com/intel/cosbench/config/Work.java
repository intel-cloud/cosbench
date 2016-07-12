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

/**
 * The model class mapping to "work" in configuration xml with following form:
 * 	<work type="type" workers="workers" division="division" 
 * 		rampup="rampup" rampdown="rampdown" runtime="runtime" config="config" ... />
 * 
 * @author ywang19, qzheng7
 *
 */
public class Work implements Iterable<Operation> {

    private String name;
    private String type = "normal";
    private int workers;
    private int interval = 5;
    private String division = "none";
    private int runtime = 0;
    private int rampup = 0;
    private int rampdown = 0;
    private int afr = -1; /* acceptable failure ratio, the unit is samples per one million,
     * default is 200000 for normal work, and 0 for init/prepare/cleanup/dispose/delay work */
    private int totalOps = 0;
    private long totalBytes = 0;
    private String driver;
    private String config = "";
    private Auth auth;
    private Storage storage;    
    private List<Operation> operations;

    public Work() {
        /* empty */
    }

    public Work(String name) {
        setName(name);
    }

    public Work(String name, String type) {
        setName(name);
        setType(type);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (StringUtils.isEmpty(name))
            throw new ConfigException("work name cannot be empty");
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        if (StringUtils.isEmpty(type))
            throw new ConfigException("work type cannot be empty");
        this.type = type;
    }

    public int getWorkers() {
        return workers;
    }

    public void setWorkers(int workers) {
        if (workers < 0)
            throw new ConfigException("illegal workers: " + workers);
        if (workers == 0)
            throw new ConfigException("must specify 'workers' for a work");
        this.workers = workers;
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
            throw new ConfigException("work must have a default division");
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

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        /* driver might be empty */
        this.driver = driver;
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
            throw new ConfigException("a work must have its auth");
        this.auth = auth;
    }

    public Storage getStorage() {
        return storage;
    }

    public void setStorage(Storage storage) {
        if (storage == null)
            throw new ConfigException("a work must have its storge");
        this.storage = storage;
    }
    
    public int getAfr() {
        return afr;
    }

    public void setAfr(int afr) {
        if (afr > 1000000 || afr < 0)
            throw new ConfigException("afr should be at 0 to 1000000 range");
        this.afr = afr;
    }    

    public List<String> getOperationIDs() {
		List<String> opIds = new ArrayList<String>();
		for (Operation operation : operations) {
			opIds.add(operation.getId());
		}
		return opIds;
	}


    public List<Operation> getOperations() {
        return operations;
    }

    public void setOperations(List<Operation> operations) {
        if (operations == null || operations.isEmpty())
            throw new ConfigException("a work must have opertations");
        for(Operation op: operations) {
        	op.setConfig(ConfigUtils.inherit(op.getConfig(), this.config));
        }
        this.operations = operations;
    }

    public void addOperation(Operation op) {
        if (op == null || op.getType().isEmpty())
            throw new ConfigException("a operation must have type");
        if (operations == null)
            operations = new ArrayList<Operation>();
        op.setConfig(ConfigUtils.inherit(op.getConfig(), this.config));
        operations.add(op);
    }

    @Override
    public Iterator<Operation> iterator() {
        return operations.iterator();
    }

    private void toPrepareWork() {
        if (name == null)
            name = "prepare";
        setDivision("object");
        setRuntime(0);
        setDefaultAfr(0);
        setTotalBytes(0);
        setTotalOps(getWorkers());
        Operation op = new Operation();
        op.setType("prepare");
        op.setRatio(100);
        Object[] cfgs = null;
        if (config.indexOf("createContainer=") < 0)
            cfgs = new Object[] { "createContainer=false", config };
        else
            cfgs = new Object[] { config };
        op.setConfig(StringUtils.join(cfgs, ';'));
        setOperations(Collections.singletonList(op));
    }

    private void toCleanupWork() {
        if (name == null)
            name = "cleanup";
        setDivision("object");
        setRuntime(0);
        setDefaultAfr(0);
        setTotalBytes(0);
        setTotalOps(getWorkers());
        Operation op = new Operation();
        op.setType("cleanup");
        op.setRatio(100);
        Object[] cfgs = null;
        if (config.indexOf("deleteContainer=") < 0)
            cfgs = new Object[] { "deleteContainer=false", config };
        else
            cfgs = new Object[] { config };
        op.setConfig(StringUtils.join(cfgs, ';'));
        setOperations(Collections.singletonList(op));
    }

    private void toInitWork() {
        if (name == null)
            name = "init";
        setDivision("container");
        setRuntime(0);
        setDefaultAfr(0);
        setTotalBytes(0);
        setTotalOps(getWorkers());
        Operation op = new Operation();
        op.setType("init");
        op.setRatio(100);
        Object[] cfgs = new Object[] { "objects=r(0,0);sizes=c(0)B", config };
        op.setConfig(StringUtils.join(cfgs, ';'));
        setOperations(Collections.singletonList(op));
    }

    private void toDisposeWork() {
        if (name == null)
            name = "dispose";
        setDivision("container");
        setRuntime(0);
        setDefaultAfr(0);
        setTotalBytes(0);
        setTotalOps(getWorkers());
        Operation op = new Operation();
        op.setType("dispose");
        op.setRatio(100);
        Object[] cfgs = new Object[] { "objects=r(0,0);sizes=c(0)B", config };
        op.setConfig(StringUtils.join(cfgs, ';'));
        setOperations(Collections.singletonList(op));
    }
    
	public void toDelayWork() {
		if (name == null)
			name = "delay";
		setDivision("none");
		setRuntime(0);
		setDefaultAfr(0);
		setTotalBytes(0);
		setWorkers(1);
		setTotalOps(getWorkers());
		Operation op = new Operation();
		op.setType("delay");
		op.setRatio(100);
		op.setConfig("");
		setOperations(Collections.singletonList(op));
	} 
	
	private void setDefaultAfr(int def) {
		if (afr < 0)
			setAfr(def);
	}

    public void validate() {
        if (type.equals("prepare"))
            toPrepareWork();
        else if (type.equals("cleanup"))
            toCleanupWork();
        else if (type.equals("init"))
            toInitWork();
        else if (type.equals("dispose"))
            toDisposeWork();
		else if (type.equals("delay"))
			toDelayWork(); 
		else 
			setDefaultAfr(200000);
        setName(getName());
        setWorkers(getWorkers());
        if (runtime == 0 && totalOps == 0 && totalBytes == 0)
            throw new ConfigException(
                    "no work limits detectd, either runtime, total ops or total bytes");
        setAuth(getAuth());
        auth.validate();
        setStorage(getStorage());
        storage.validate();
        List<Operation> tempOpList = new ArrayList<Operation>();
        for (Operation op: operations) {
        	if(op.getRatio() > 0) {
        		tempOpList.add(op);
        	}
        }
        operations = tempOpList;
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
