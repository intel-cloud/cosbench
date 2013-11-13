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

import static com.intel.cosbench.driver.operator.Deleter.doDelete;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.intel.cosbench.api.storage.StorageException;
import com.intel.cosbench.api.storage.StorageInterruptedException;
import com.intel.cosbench.bench.*;
import com.intel.cosbench.config.Config;
import com.intel.cosbench.driver.agent.AgentException;
import com.intel.cosbench.driver.util.ObjectScanner;
import com.intel.cosbench.service.AbortedException;

/**
 * This class encapsulates operations to delete objects, essentially, it maps to
 * primitive DELETE operation.
 * 
 * @author ywang19, qzheng7
 * 
 */
class Cleaner extends AbstractOperator {

    public static final String OP_TYPE = "cleanup";

    private boolean deleteContainer;
    private ObjectScanner objScanner = new ObjectScanner();

    public Cleaner() {
        /* empty */
    }

    @Override
    protected void init(String id, int ratio, String division, Config config) {
        super.init(id, ratio, division, config);
        objScanner.init(division, config);
        deleteContainer = config.getBoolean("deleteContainer", true);
    }

    @Override
    public String getOpType() {
        return OP_TYPE;
    }

    @Override
    public String getSampleType() {
        return Deleter.OP_TYPE;
    }

    @Override
    protected void operate(int idx, int all, Session session) {
        String[] path = null;
        String opType = getOpType();
        String lastContainer = null;

        while ((path = objScanner.nextObjPath(path, idx, all)) != null) {
            if (deleteContainer && !StringUtils.equals(lastContainer, path[0])) {
                if (lastContainer != null)
                    doDispose(lastContainer, config, session);
                lastContainer = path[0];
            }
            if (path[1] == null)
                continue;
            Sample sample = doDelete(path[0], path[1], config, session, this);
            sample.setOpType(opType);
            session.getListener().onSampleCreated(sample);
        }

        if (deleteContainer && lastContainer != null)
            doDispose(lastContainer, config, session);

        Date now = new Date();
		Result result = new Result(now, getId(), getOpType(), getSampleType(),
				getName(), true);
        session.getListener().onOperationCompleted(result);
    }

    public static void doDispose(String conName, Config config, Session session) {
        if (Thread.interrupted())
            throw new AbortedException();

        try {
            session.getApi().deleteContainer(conName, config);
        } catch (StorageInterruptedException sie) {
            throw new AbortedException();
        } catch (StorageException se) {
            String msg = "Error deleting container " +  conName; 
            doLogWarn(session.getLogger(), msg);
            // ignored
        } catch (Exception e) {
            doLogErr(session.getLogger(), "fail to perform clean operation", e);
            throw new AgentException(); // mark error
        }

        /* no sample is provided for this operation */
    }

}
