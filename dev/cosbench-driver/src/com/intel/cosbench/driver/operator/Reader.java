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
    
    private boolean hashCheck = false;

    private ObjectPicker objPicker = new ObjectPicker();
    
    private byte buffer[] = new byte[1024*1024];

    public Reader() {
        /* empty */
    }

    @Override
    protected void init(String id, int ratio, String division, Config config) {
        super.init(id, ratio, division, config);
        objPicker.init(division, config);
        hashCheck = config.getBoolean("hashCheck", false);
    }

    @Override
    public String getOpType() {
        return OP_TYPE;
    }

    @Override
    protected void operate(int idx, int all, Session session) {
        String[] path = objPicker.pickObjPath(session.getRandom(), idx, all);
        NullOutputStream out = new NullOutputStream();
        Sample sample = doRead(out, path[0], path[1], config, session);
        session.getListener().onSampleCreated(sample);
        Date now = sample.getTimestamp();
		Result result = new Result(now, getId(), getOpType(), getSampleType(),
				getName(), sample.isSucc());
        session.getListener().onOperationCompleted(result);
    }

    private Sample doRead(OutputStream out, String conName, String objName,
            Config config, Session session) {
        if (Thread.interrupted())
            throw new AbortedException();

        InputStream in = null;
        CountingOutputStream cout = new CountingOutputStream(out);

        long start = System.currentTimeMillis();
        long xferTime = 0L;
        long xferTimeCheck = 0L;
        try {
            in = session.getApi().getObject(conName, objName, config);
            long xferStart = System.currentTimeMillis();
            if (!hashCheck){
                copyLarge(in, cout);
            	long xferEnd = System.currentTimeMillis();
            	xferTime = xferEnd - xferStart;
            } else if (!validateChecksum(conName, objName, session, in, cout, xferTimeCheck))
				return new Sample(new Date(), getId(), getOpType(),
						getSampleType(), getName(), false);
        } catch (StorageInterruptedException sie) {
            doLogErr(session.getLogger(), sie.getMessage(), sie);
            throw new AbortedException();
        } catch (Exception e) {
        	isUnauthorizedException(e, session);
        	errorStatisticsHandle(e, session, conName + "/" + objName);
        	
            return new Sample(new Date(), getId(), getOpType(), getSampleType(), getName(), false);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(cout);
        }
        long end = System.currentTimeMillis();

        Date now = new Date(end);
		return new Sample(now, getId(), getOpType(), getSampleType(),
				getName(), true, end - start, hashCheck ? xferTimeCheck : xferTime, cout.getByteCount());
    }

    public OutputStream copyLarge(InputStream input, OutputStream output)
            throws IOException
    {
            for(int n = 0; -1 != (n = input.read(buffer));)
            {
                output.write(buffer, 0, n);
            }

            return output;
    }
    
    private static boolean validateChecksum(String conName, String objName,
            Session session, InputStream in, OutputStream out, long xferTimeCheck)
            throws IOException {
        HashUtil util;
        try {
            util = new HashUtil();
            int hashLen = util.getHashLen();

            byte buf1[] = new byte[4096];
            byte buf2[] = new byte[4096];

            String storedHash = new String();
            String calculatedHash = new String();
            
            long xferStart = System.currentTimeMillis();
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
            xferTimeCheck = System.currentTimeMillis() - xferStart;
            
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
