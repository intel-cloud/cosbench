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

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.*;
import org.apache.commons.lang.StringUtils;

import static com.intel.cosbench.driver.util.Defaults.OPERATION_PREFIX;
import static com.intel.cosbench.driver.util.Defaults.OPERATION_SUFFIX;
import com.intel.cosbench.api.storage.StorageInterruptedException;
import com.intel.cosbench.bench.*;
import com.intel.cosbench.config.Config;
import com.intel.cosbench.driver.util.*;
import com.intel.cosbench.service.AbortedException;

/**
 * This class represents primitive READ operation.
 * 
 * @author ywang19, qzheng7
 * 
 */
class Reader extends AbstractOperator {

    public static final String OP_TYPE = "read";
    
    public String name;

    private boolean hashCheck = false;

    private ObjectPicker objPicker = new ObjectPicker();

    public Reader() {
        /* empty */
    }

    @Override
    protected void init(String division, Config config) {
        super.init(division, config);
        objPicker.init(division, config);
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
    public String getSampleType() {
        return getName();
    }

    @Override
    protected void operate(int idx, int all, Session session) {
        String[] path = objPicker.pickObjPath(session.getRandom(), idx, all);
        NullOutputStream out = new NullOutputStream();
        Sample sample = doRead(out, path[0], path[1], config, session);
        session.getListener().onSampleCreated(sample);
        Date now = sample.getTimestamp();
        Result result = new Result(now, name, sample.isSucc());
        session.getListener().onOperationCompleted(result);
    }

    private Sample doRead(OutputStream out, String conName, String objName,
            Config config, Session session) {
        if (Thread.interrupted())
            throw new AbortedException();

        InputStream in = null;
        CountingOutputStream cout = new CountingOutputStream(out);

        long start = System.currentTimeMillis();

        try {
            in = session.getApi().getObject(conName, objName, config);
            if (!hashCheck)
                IOUtils.copyLarge(in, cout);
            else if (!validateChecksum(conName, objName, session, in, cout))
                return new Sample(new Date(), name, false);
        } catch (StorageInterruptedException sie) {
            throw new AbortedException();
        } catch (Exception e) {
            doLogErr(session.getLogger(), "fail to perform read operation", e);
            return new Sample(new Date(), name, false);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(cout);
        }

        long end = System.currentTimeMillis();

        Date now = new Date(end);
        return new Sample(now, name, true, end - start, cout.getByteCount());
    }

    private static boolean validateChecksum(String conName, String objName,
            Session session, InputStream in, OutputStream out)
            throws IOException {
        HashUtil util;
        try {
            util = new HashUtil();
            int hashLen = util.getHashLen();

            byte buf1[] = new byte[4096];
            byte buf2[] = new byte[4096];

            String storedHash = new String();
            String calculatedHash = new String();
            int br1 = in.read(buf1);

            if (br1 <= hashLen) {
                out.write(buf1, 0, br1);
                String warn = "The size is too small to embed checksum, will skip integrity checking.";
                doLogWarn(session.getLogger(), warn);
            }

            while (br1 > hashLen) { // hash is attached in the end.
                int br2 = in.read(buf2);

                if (br2 < 0) { // reach end of stream
                    out.write(buf1, 0, br1);
                    util.update(buf1, 0, br1 - hashLen);

                    calculatedHash = util.calculateHash();
                    storedHash = new String(buf1, br1 - hashLen, hashLen);

                    br1 = 0;
                } else if (br2 <= hashLen) {
                    out.write(buf1, 0, br1 + br2);
                    util.update(buf1, 0, br1 + br2 - hashLen);

                    calculatedHash = util.calculateHash();
                    storedHash = new String(buf1, br1 + br2 - hashLen, hashLen - br2) + new String(buf2, 0, br2);

                    br1 = 0;
                } else {
                    out.write(buf1, 0, br1);
                    util.update(buf1, 0, br1);

                    System.arraycopy(buf2, 0, buf1, 0, br2);

                    br1 = br2;
                }
            }

            if (!calculatedHash.equals(storedHash)) {
                if (storedHash.startsWith(HashUtil.GUARD)) {
                    String err =
                            "Inconsistent Hashes for " + conName + "\\" + objName + ": calculated=" + calculatedHash
                                    + ", stored=" + storedHash;
                    doLogErr(session.getLogger(), err);
                    return false;
                } else {
                    String warn = "No checksum embedded in " + conName + "\\" + objName;
                    doLogWarn(session.getLogger(), warn);
                }
            }

            return true; /* checksum - okay */
        } catch (NoSuchAlgorithmException e) {
            doLogErr(session.getLogger(), "Alogrithm not found", e);
        }
        return false; // if we reach this, something went wrong when trying to calculate the hash
    }

}
