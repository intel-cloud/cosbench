/**

Copyright 2021-2022 eHualu Corporation, All Rights Reserved.

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

import com.intel.cosbench.api.storage.StorageException;
import com.intel.cosbench.api.storage.StorageInterruptedException;
import com.intel.cosbench.api.storage.StorageTimeoutException;
import com.intel.cosbench.bench.Result;
import com.intel.cosbench.bench.Sample;
import com.intel.cosbench.config.Config;
import com.intel.cosbench.driver.generator.RandomInputStream;
import com.intel.cosbench.driver.generator.XferCountingInputStream;
import com.intel.cosbench.driver.util.ObjectPicker;
import com.intel.cosbench.driver.util.SizePicker;
import com.intel.cosbench.service.AbortedException;

/**
 * This class represents primitive Multipart WRITE operation.
 *
 * @author Sine
 *
 */
class MWriter extends AbstractOperator {

    public static final String OP_TYPE = "mwrite";

    private boolean chunked;
    private boolean isRandom;
    private boolean hashCheck = false;
    private ObjectPicker objPicker = new ObjectPicker();
    private SizePicker sizePicker = new SizePicker();

    public MWriter() {
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
        Sample sample = doMWrite(in, len, path[0], path[1], config, session,
                this);
        session.getListener().onSampleCreated(sample);
        Date now = sample.getTimestamp();
        Result result = new Result(now, getId(), getOpType(), getSampleType(),
                getName(), sample.isSucc());
        session.getListener().onOperationCompleted(result);
    }

    public static  Sample doMWrite(InputStream in, long length, String conName,
            String objName, Config config, Session session, Operator op) {
        if (Thread.interrupted())
            throw new AbortedException();

        XferCountingInputStream cin = new XferCountingInputStream(in);
        long start = System.nanoTime();

        try {
            session.getApi().createMultipartObject(conName, objName, cin, length, config);
        } catch (StorageInterruptedException sie) {
            doLogErr(session.getLogger(), sie.getMessage(), sie);
            throw new AbortedException();
        } catch (StorageTimeoutException ste) {
        	String msg = "Error multipart-upload-object " + conName + "/" + objName + " " + ste.getMessage();
			doLogWarn(session.getLogger(), msg);
        	
			return new Sample(new Date(), op.getId(), op.getOpType(),
                    op.getSampleType(), op.getName(), false);
		} catch (StorageException se) {
			String msg = "Error multipart-upload-object " + conName + "/" + objName + " " + se.getMessage();
			doLogWarn(session.getLogger(), msg);
			
			return new Sample(new Date(), op.getId(), op.getOpType(),
                    op.getSampleType(), op.getName(), false);
		} catch (Exception e) {
            isUnauthorizedException(e, session);
            errorStatisticsHandle(e, session, conName + "/" + objName);

            return new Sample(new Date(), op.getId(), op.getOpType(),
                    op.getSampleType(), op.getName(), false);

        } finally {
            IOUtils.closeQuietly(cin);
        }

        long end = System.nanoTime();
        return new Sample(new Date(), op.getId(), op.getOpType(), op.getSampleType(),
                op.getName(), true, (end - start) / 1000000,
                cin.getXferTime(), cin.getByteCount());
    }
}
