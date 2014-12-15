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

import java.io.InputStream;
import java.util.Date;
import java.util.Random;

import org.apache.commons.io.IOUtils;

import com.intel.cosbench.api.storage.StorageInterruptedException;
import com.intel.cosbench.bench.Result;
import com.intel.cosbench.bench.Sample;
import com.intel.cosbench.config.Config;
import com.intel.cosbench.driver.generator.RandomInputStream;
import com.intel.cosbench.driver.generator.XferCountingInputStream;
import com.intel.cosbench.driver.util.ObjectPicker;
import com.intel.cosbench.driver.util.SizePicker;
import com.intel.cosbench.service.AbortedException;

/**
 * This class represents primitive WRITE operation.
 * 
 * @author ywang19, qzheng7
 * 
 */
class Writer extends AbstractOperator {

    public static final String OP_TYPE = "write";

    private boolean chunked;
    private boolean isRandom;
    private boolean hashCheck = false;
    private ObjectPicker objPicker = new ObjectPicker();
    private SizePicker sizePicker = new SizePicker();

    public Writer() {
        /* empty */
    }

    @Override
    protected void init(String id, int ratio, String division, Config config) {
        super.init(id, ratio, division, config);
        objPicker.init(division, config);
        sizePicker.init(config);
        chunked = config.getBoolean("chunked", false);
        isRandom = !config.get("content", "random").equals("zero");
        hashCheck = config.getBoolean("hashCheck", false);
    }

    @Override
    public String getOpType() {
        return OP_TYPE;
    }

    @Override
    protected void operate(int idx, int all, Session session) {
        Random random = session.getRandom();
        long size = sizePicker.pickObjSize(random);
        long len = chunked ? -1 : size;
        String[] path = objPicker.pickObjPath(random, idx, all);
        RandomInputStream in = new RandomInputStream(size, random, isRandom,
                hashCheck);
		Sample sample = doWrite(in, len, path[0], path[1], config, session,
				this);
        session.getListener().onSampleCreated(sample);
        Date now = sample.getTimestamp();
		Result result = new Result(now, getId(), getOpType(), getSampleType(),
				getName(), sample.isSucc());
        session.getListener().onOperationCompleted(result);
    }
    
    public static  Sample doWrite(InputStream in, long length, String conName,
            String objName, Config config, Session session, Operator op) {
        if (Thread.interrupted())
            throw new AbortedException();
        
        XferCountingInputStream cin = new XferCountingInputStream(in);	
        long start = System.currentTimeMillis();

        try {
            session.getApi()
                    .createObject(conName, objName, cin, length, config);
        } catch (StorageInterruptedException sie) {
            doLogErr(session.getLogger(), sie.getMessage(), sie);
            throw new AbortedException();
        } catch (Exception e) {
        	isUnauthorizedException(e, session);
        	errorStatisticsHandle(e, session, conName + "/" + objName);
        	
			return new Sample(new Date(), op.getId(), op.getOpType(),
					op.getSampleType(), op.getName(), false);
			
        } finally {
            IOUtils.closeQuietly(cin);
        }

        long end = System.currentTimeMillis();
        Date now = new Date(end);
		return new Sample(now, op.getId(), op.getOpType(), op.getSampleType(),
				op.getName(), true, end - start, cin.getXferTime(), cin.getByteCount());
    }
    /*
     * public static Sample doWrite(byte[] data, String conName, String objName,
     * Config config, Session session) { if (Thread.interrupted()) throw new
     * AbortedException();
     * 
     * long start = System.currentTimeMillis();
     * 
     * try { session.getApi().createObject(conName, objName, data, config); }
     * catch (StorageInterruptedException sie) { throw new AbortedException(); }
     * catch (Exception e) { doLog(session.getLogger(),
     * "fail to perform write operation", e); return new Sample(new Date(),
     * OP_TYPE, false); }
     * 
     * long end = System.currentTimeMillis();
     * 
     * Date now = new Date(end); return new Sample(now, OP_TYPE, true, end -
     * start, data.length); }
     */
}
