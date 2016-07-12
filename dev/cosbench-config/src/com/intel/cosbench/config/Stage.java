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
import com.intel.cosbench.log.LogFactory;
import com.intel.cosbench.log.Logger;

/**
 * The model class mapping to "workstage" in configuration xml with following form:
 * 	<workstage name="name" />
 * 
 * @author ywang19, qzheng7
 *
 */
public class Stage implements Iterable<Work> {

    private String name;
    private int closuredelay;
    private String trigger=null;
    private String config = "";
	private Auth auth;
    private Storage storage;
    private List<Work> works;

    public Stage() {
        /* empty */
    }

    public Stage(String name) {
        setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (StringUtils.isEmpty(name))
            throw new ConfigException("stage name cannot be empty");
        if (StringUtils.containsAny(name, ConfigConstants.DELIMITER))
        	throw new ConfigException("stage name cannot contain delimiter '" + ConfigConstants.DELIMITER + "'");
        this.name = name;
    }
    
	public int getClosuredelay() {
		return closuredelay;
	}
	
    public String getTrigger() {
		return trigger;
	}

	public void setTrigger(String trigger) {
		this.trigger = trigger;
	}

	public String getConfig() {
		return config;
	}
	
	public void setConfig(String config) {
		this.config = config;
	}

	public void setClosuredelay(int closuredelay) {
		if (closuredelay < 0)
			throw new ConfigException("closure delay cannot be negative");
		this.closuredelay = closuredelay;
	} 

    public Auth getAuth() {
        return auth;
    }

    public void setAuth(Auth auth) {
        if (auth == null)
            throw new ConfigException("a stage must have a default auth");
        this.auth = auth;
    }

    public Storage getStorage() {
        return storage;
    }

    public void setStorage(Storage storage) {
        if (storage == null)
            throw new ConfigException("a stage must have a default storage");
//        if(!name.equals("init") && !name.equals("dispose"))
//            this.storage=removeNSROOTConfig(storage);
//        else
            this.storage = storage;
    }
    
	// method for removing nsroot config from prepare, normal and cleanup stages
//	private Storage removeNSROOTConfig(Storage storage) {
//		if(storage.getConfig() == null)
//			return storage;
//		if (!storage.getConfig().contains("nsroot"))
//			return storage;
//		else {
//			Storage newStorage = new Storage();
//			String configParams[] = storage.getConfig().split(";");
//			StringBuffer newConfig = new StringBuffer("");
//			for (String configParam : configParams) {
//				if (!configParam.toLowerCase().contains("nsroot"))
//					newConfig.append(configParam + ";");
//			}
//			newConfig.deleteCharAt(newConfig.length() - 1);
//			newStorage.setType(storage.getType());
//			newStorage.setConfig(newConfig.toString());
//			return newStorage;
//		}
//	}

    public List<Work> getWorks() {
        return works;
    }

    public void setWorks(List<Work> works) {
        if (works == null || works.isEmpty())
            throw new ConfigException("stage must have works");
        for(Work work: works) {
        	work.setConfig(ConfigUtils.inherit(work.getConfig(), this.config));
        	 Logger logger = LogFactory.getSystemLogger();
     		logger.debug("stage config: "+this.config+ "work inherit result: "+  ConfigUtils.inherit(work.getConfig(), this.config));
         
        }
        this.works = works;
    }

    public void addWork(Work work) {
        if (work == null || work.getName().isEmpty())
            throw new ConfigException("can't add one empty work");
        if (works == null)
            works = new ArrayList<Work>();
        work.setConfig(ConfigUtils.inherit(work.getConfig(), this.config));
        works.add(work);
    }

    @Override
    public Iterator<Work> iterator() {
        return works.iterator();
    }

    public void validate() {
        setName(getName());
        setAuth(getAuth());
        setStorage(getStorage());
        setWorks(getWorks());
        for (Work work : works)
            if (work.getAuth() == null)
                work.setAuth(auth);
        for (Work work : works)
            if (work.getStorage() == null)
                work.setStorage(storage);
        for (Work work : works)
            work.validate();
    }

}
