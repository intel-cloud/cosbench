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
import java.io.InputStream;
import java.util.Date;
import java.util.Random;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.CountingInputStream;

import com.intel.cosbench.api.storage.StorageInterruptedException;
import com.intel.cosbench.bench.Result;
import com.intel.cosbench.bench.Sample;
import com.intel.cosbench.config.Config;
import com.intel.cosbench.driver.util.ContainerPicker;
import com.intel.cosbench.driver.util.FilePicker;
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

    public FileWriter() {
        /* empty */
    }

    @Override
    protected void init(String division, Config config) {
        super.init(division, config);
        contPicker.init(division, config);
        String filepath = config.get("files");
        folder = new File(filepath);
        listOfFiles = folder.listFiles();
        System.out.println("listOfFiles.length: " + listOfFiles.length);
        String range = "(1," + listOfFiles.length + ")";
        System.out.println("initialising filePicker, range: " + range);
        filePicker.init(range, config);
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
            sample = new Sample(new Date(), OP_TYPE, false);
        }

        // looks kinda ugly, but does make sense…

        Random random = session.getRandom();
        String containerName = contPicker.pickContName(random, idx, all);

        // as I said, the 1 from init() is being removed here
        Integer rand = (filePicker.pickObjKey(random) - 1);
        String filename = null;
        try {
            filename = listOfFiles[rand].getName();
        } catch (ArrayIndexOutOfBoundsException e) {
            doLogErr(session.getLogger(), "fail to perform file Write operation, tried to put more files than exist", e);
            sample = new Sample(new Date(), OP_TYPE, false);
            session.getListener().onSampleCreated(sample);
            Date now = sample.getTimestamp();
            Result result = new Result(now, OP_TYPE, sample.isSucc());
            session.getListener().onOperationCompleted(result);
            return;
        }

        try {
            FileInputStream fis = new FileInputStream(listOfFiles[rand]);
            sample = doWrite(fis, listOfFiles[rand].length(), containerName, filename, config, session);
            System.out.println("rand: " + rand + " filename: " + filename + " container " + containerName);
        } catch (FileNotFoundException e) {
            doLogErr(session.getLogger(), "fail to perform file Write operation", e);
            System.out.println("fail to perform file Write operation " + e.getMessage());
            sample = new Sample(new Date(), OP_TYPE, false);
        }
        session.getListener().onSampleCreated(sample);
        Date now = sample.getTimestamp();
        Result result = new Result(now, OP_TYPE, sample.isSucc());
        session.getListener().onOperationCompleted(result);
    }

    public static Sample doWrite(InputStream in, long length, String conName, String objName, Config config, Session session) {
        if (Thread.interrupted())
            throw new AbortedException();

        CountingInputStream cin = new CountingInputStream(in);

        long start = System.currentTimeMillis();

        try {
            session.getApi().createObject(conName, objName, cin, length, config);
        } catch (StorageInterruptedException sie) {
            throw new AbortedException();
        } catch (Exception e) {
            session.getLogger().error("fail to perform write operation", e);
            return new Sample(new Date(), OP_TYPE, false);
        } finally {
            IOUtils.closeQuietly(cin);
        }

        long end = System.currentTimeMillis();

        Date now = new Date(end);
        return new Sample(now, OP_TYPE, true, end - start, cin.getByteCount());
    }
}
