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

package com.intel.cosbench.driver.operator;

import static com.intel.cosbench.driver.util.Defaults.OPERATION_PREFIX;
import static com.intel.cosbench.driver.util.Defaults.OPERATION_SUFFIX;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.intel.cosbench.api.storage.StorageInterruptedException;
import com.intel.cosbench.bench.*;
import com.intel.cosbench.config.Config;
import com.intel.cosbench.driver.util.ObjectPicker;
import com.intel.cosbench.service.AbortedException;

/**
 * This class represents primitive DELETE operation.
 * 
 * @author ywang19, qzheng7
 * 
 */
class Deleter extends AbstractOperator {

    public static final String OP_TYPE = "delete";
    public String name;

    private ObjectPicker objPicker = new ObjectPicker();

    public Deleter() {
        /* empty */
    }

    @Override
    protected void init(String division, Config config) {
        super.init(division, config);
        objPicker.init(division, config);
		name = StringUtils.join(new Object[] {
				config.get("opprefix", OPERATION_PREFIX), OP_TYPE,
				config.get("opsuffix", OPERATION_SUFFIX) });
    }

    @Override
    public String getOpType() {
        return OP_TYPE;
    }
    
	@Override
	public String getName() {
		return name;
	}
	
    @Override
    public String getSampleType() {
        return getName();
    }

    @Override
    protected void operate(int idx, int all, Session session) {
        String[] path = objPicker.pickObjPath(session.getRandom(), idx, all);
        Sample sample = doDelete(path[0], path[1], config, session, name);
        session.getListener().onSampleCreated(sample);
        Date now = sample.getTimestamp();
        Result result = new Result(now, name, sample.isSucc());
        session.getListener().onOperationCompleted(result);
    }

    public static Sample doDelete(String conName, String objName,
			Config config, Session session) {
		return doDelete(conName, objName, config, session, OP_TYPE);
	}
    
    public static Sample doDelete(String conName, String objName,
            Config config, Session session, String opName) {
        if (Thread.interrupted())
            throw new AbortedException();

        long start = System.currentTimeMillis();

        try {
            session.getApi().deleteObject(conName, objName, config);
        } catch (StorageInterruptedException sie) {
            throw new AbortedException();
        } catch (Exception e) {
            doLogErr(session.getLogger(), "fail to perform remove operation", e);
            return new Sample(new Date(), opName, false);
        }

        long end = System.currentTimeMillis();

        Date now = new Date(end);
        return new Sample(now, opName, true, end - start, 0L);
    }

}
