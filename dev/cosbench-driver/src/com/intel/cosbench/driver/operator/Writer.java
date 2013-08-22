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
import java.util.*;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.CountingInputStream;
import org.apache.commons.lang.StringUtils;

import static com.intel.cosbench.driver.util.Defaults.OPERATION_PREFIX;
import static com.intel.cosbench.driver.util.Defaults.OPERATION_SUFFIX;
import com.intel.cosbench.api.storage.StorageInterruptedException;
import com.intel.cosbench.bench.*;
import com.intel.cosbench.config.Config;
import com.intel.cosbench.driver.random.RandomInputStream;
import com.intel.cosbench.driver.util.*;
import com.intel.cosbench.service.AbortedException;

/**
 * This class represents primitive WRITE operation.
 * 
 * @author ywang19, qzheng7
 * 
 */
class Writer extends AbstractOperator {

    public static final String OP_TYPE = "write";
    public String name;

    private boolean chunked;
    private boolean isRandom;
    private boolean hashCheck = false;
    private ObjectPicker objPicker = new ObjectPicker();
    private SizePicker sizePicker = new SizePicker();

    public Writer() {
        /* empty */
    }

    @Override
    protected void init(String division, Config config) {
        super.init(division, config);
        objPicker.init(division, config);
        sizePicker.init(config);
        chunked = config.getBoolean("chunked", false);
        isRandom = !config.get("content", "random").equals("zero");
        hashCheck = config.getBoolean("hashCheck", false);
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
    protected void operate(int idx, int all, Session session) {
        Random random = session.getRandom();
        long size = sizePicker.pickObjSize(random);
        long len = chunked ? -1 : size;
        String[] path = objPicker.pickObjPath(random, idx, all);
        RandomInputStream in = new RandomInputStream(size, random, isRandom,
                hashCheck);
		Sample sample = doWrite(in, len, path[0], path[1], config, session,
				name);
        session.getListener().onSampleCreated(sample);
        Date now = sample.getTimestamp();
        Result result = new Result(now, name, sample.isSucc());
        session.getListener().onOperationCompleted(result);
    }

    public static Sample doWrite(InputStream in, long length, String conName,
			String objName, Config config, Session session) {
		return doWrite(in, length, conName, objName, config, session, OP_TYPE);
	}
    
    public static  Sample doWrite(InputStream in, long length, String conName,
            String objName, Config config, Session session, String opName) {
        if (Thread.interrupted())
            throw new AbortedException();

        CountingInputStream cin = new CountingInputStream(in);

        long start = System.currentTimeMillis();

        try {
            session.getApi()
                    .createObject(conName, objName, cin, length, config);
        } catch (StorageInterruptedException sie) {
            throw new AbortedException();
        } catch (Exception e) {
            session.getLogger().error("fail to perform write operation", e);
            return new Sample(new Date(), opName, false);
        } finally {
            IOUtils.closeQuietly(cin);
        }

        long end = System.currentTimeMillis();

        Date now = new Date(end);
        return new Sample(now, opName, true, end - start, cin.getByteCount());
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
