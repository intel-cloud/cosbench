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

import static com.intel.cosbench.driver.operator.Writer.doWrite;

import java.util.*;

import org.apache.commons.lang.StringUtils;

import com.intel.cosbench.api.auth.AuthException;
import com.intel.cosbench.api.storage.StorageException;
import com.intel.cosbench.api.storage.StorageInterruptedException;
import com.intel.cosbench.bench.*;
import com.intel.cosbench.config.Config;
import com.intel.cosbench.driver.agent.AgentException;
import com.intel.cosbench.driver.generator.RandomInputStream;
import com.intel.cosbench.driver.util.*;
import com.intel.cosbench.service.AbortedException;

/**
 * This class encapsulates operations to create objects, essentially, it maps to
 * primitive WRITE operation.
 * 
 * @author ywang19, qzheng7
 * 
 */
class Preparer extends AbstractOperator {

    public static final String OP_TYPE = "prepare";

    private boolean chunked;
    private boolean isRandom;
    private boolean createContainer;
    private boolean hashCheck = false;
    private ObjectScanner objScanner = new ObjectScanner();
    private SizePicker sizePicker = new SizePicker();

    public Preparer() {
        /* empty */
    }

    @Override
    protected void init(String id, int ratio, String division, Config config) {
    	super.init(id, ratio, division, config);
        objScanner.init(division, config);
        sizePicker.init(config);
        chunked = config.getBoolean("chunked", false);
        isRandom = !config.get("content", "random").equals("zero");
        createContainer = config.getBoolean("createContainer", true);
        hashCheck = config.getBoolean("hashCheck", false);
    }

    @Override
    public String getOpType() {
        return OP_TYPE;
    }

    @Override
    public String getSampleType() {
        return Writer.OP_TYPE;
    }

    @Override
    protected void operate(int idx, int all, Session session) {
        String[] path = null;
        String opTye = getOpType();
        String lastContainer = null;

        while ((path = objScanner.nextObjPath(path, idx, all)) != null) {
            if (createContainer && !StringUtils.equals(lastContainer, path[0])) {
                doInit(path[0], config, session);
                lastContainer = path[0];
            }
            if (path[1] == null)
                continue;
            Random random = session.getRandom();
            long size = sizePicker.pickObjSize(random);
            long len = chunked ? -1 : size;
            RandomInputStream in = new RandomInputStream(size, random,
                    isRandom, hashCheck);
            Sample sample = doWrite(in, len, path[0], path[1], config, session, this);
            sample.setOpType(opTye);
            session.getListener().onSampleCreated(sample);
        }

        Date now = new Date();
		Result result = new Result(now, getId(), getOpType(), getSampleType(),
				getName(), true);
        session.getListener().onOperationCompleted(result);
    }

    public static void doInit(String conName, Config config, Session session) {
        if (Thread.interrupted())
            throw new AbortedException();

        try {
        	session.getApi().createContainer(conName, config);
        } catch (StorageInterruptedException sie) {
            doLogErr(session.getLogger(), sie.getMessage(), sie);
            throw new AbortedException();
        }catch(StorageException se) {
        	isUnauthorizedException(se, session);
            errorStatisticsHandle(se, session, conName);
            if(session.getApi().isAuthValid()){
            	throw new AgentException();
            }
            else {
				throw new AuthException(se);
			}
        }catch (Exception e) {
            throw new AgentException(); // mark error
        }

        /* no sample is provided for this operation */
    }

}
