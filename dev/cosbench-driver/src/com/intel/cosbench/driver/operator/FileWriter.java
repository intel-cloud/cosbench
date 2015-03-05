/** 
 
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import org.apache.commons.io.IOUtils;

import com.intel.cosbench.api.storage.StorageInterruptedException;
import com.intel.cosbench.bench.Result;
import com.intel.cosbench.bench.Sample;
import com.intel.cosbench.config.Config;
import com.intel.cosbench.driver.generator.XferCountingInputStream;
import com.intel.cosbench.driver.util.ContainerPicker;
import com.intel.cosbench.driver.util.FilePicker;
import com.intel.cosbench.driver.util.HashUtil;
import com.intel.cosbench.driver.util.HashedFileInputStream;
import com.intel.cosbench.service.AbortedException;

/**
 * This class represents a WRITE operation that reads a specified folder and writes the files contained.
 * 
 * @author Niklas Goerke niklas974@github
 * 
 */
class FileWriter extends AbstractOperator {

    public static final String OP_TYPE = "filewrite";

    private ContainerPicker contPicker = new ContainerPicker();
    private FilePicker filePicker = new FilePicker();

    private File folder;

    private File[] listOfFiles;
    private boolean hashCheck = false;

    public FileWriter() {
        /* empty */
    }

    @Override
    protected void init(String id, int ratio, String division, Config config) {
        super.init(id, ratio, division, config);
        contPicker.init(division, config);
        String filepath = config.get("files");
        folder = new File(filepath);
        if (!folder.exists()) {
            throw new RuntimeException("Folder " + filepath + " does not exist.");
        }
        listOfFiles = folder.listFiles();
        Arrays.sort(listOfFiles);
        String range = "(1," + listOfFiles.length + ")";
        filePicker.init(range, config);
        hashCheck = config.getBoolean("hashCheck", false);
    }

    @Override
    public String getOpType() {
        return OP_TYPE;
    }

    @Override
    protected void operate(int idx, int all, Session session) {
        Sample sample;
        if (!folder.canRead()) {
            doLogErr(session.getLogger(), "fail to perform file filewrite operation, can not read " + folder.getAbsolutePath());
			sample = new Sample(new Date(), getId(), getOpType(),
					getSampleType(), getName(), false);
        }
        Random random = session.getRandom();
        String containerName = contPicker.pickContName(random, idx, all);
        
        // as we index arrays starting from 0, we need to remove 1 here
        Integer rand = (filePicker.pickObjKey(random) - 1);
        String filename = null;
        
        InputStream fis = null;
        filename = listOfFiles[rand].getName();
        long length = listOfFiles[rand].length();
        
        try {
            if (hashCheck) {
                HashUtil util = new HashUtil();
                int hashLen = util.getHashLen();
                length += hashLen;
                fis = new HashedFileInputStream(listOfFiles[rand], hashCheck, util, length);
            } else {
                fis = new FileInputStream(listOfFiles[rand]);
            }

            sample = doWrite(fis, length, containerName, filename, config, session);
        } catch (FileNotFoundException e) {
            doLogErr(session.getLogger(), "failed to perform file Write operation, file not found", e);
			sample = new Sample(new Date(), getId(), getOpType(),
					getSampleType(), getName(), false);
        } catch (ArrayIndexOutOfBoundsException e) {
            doLogErr(session.getLogger(), "failed to perform file Write operation, tried to put more files than exist", e);
            sample = new Sample(new Date(),  getId(), getOpType(),
					getSampleType(), getName(), false);
        } catch (NoSuchAlgorithmException e) {
            doLogErr(session.getLogger(),
                    "failed to perform file Write operation, hash Algorithm MD5 not supported, deaktivate hashCheck, maybe?", e);
            sample = new Sample(new Date(), getId(), getOpType(),
					getSampleType(), getName(), false);
        }finally {
        	if(fis != null) {
        		try {
					fis.close();
				} catch (IOException e) {
					doLogErr(session.getLogger(), "failed to close file " + filename, e);
				}
        	}
        }

        session.getListener().onSampleCreated(sample);
        Date now = sample.getTimestamp();
		Result result = new Result(now, getId(), getOpType(), getSampleType(),
				getName(), sample.isSucc());
        session.getListener().onOperationCompleted(result);
    }

    public Sample doWrite(InputStream in, long length, String conName, String objName, Config config, Session session) {
        if (Thread.interrupted())
            throw new AbortedException();

        XferCountingInputStream cin = new XferCountingInputStream(in);

        long start = System.currentTimeMillis();

        try {
              session.getApi().createObject(conName, objName, cin, length, config);
        } catch (StorageInterruptedException sie) {
            doLogErr(session.getLogger(), sie.getMessage(), sie);
            throw new AbortedException();
        } catch (Exception e) {
        	isUnauthorizedException(e, session);
            doLogErr(session.getLogger(), "fail to perform filewrite operation", e);
            return new Sample(new Date(), getId(), getOpType(), getSampleType(),
    				getName(), false);
        } finally {
            IOUtils.closeQuietly(cin);
        }

        long end = System.currentTimeMillis();

        Date now = new Date(end);
        return new Sample(now,  getId(), getOpType(), getSampleType(),
				getName(), true, end - start, cin.getXferTime(), cin.getByteCount());
    }
}
