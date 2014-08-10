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

package com.intel.cosbench.api.mock;

import static com.intel.cosbench.api.mock.MockConstants.*;

import java.io.InputStream;
import java.util.Random;

import org.apache.commons.io.input.NullInputStream;

import com.intel.cosbench.api.storage.*;
import com.intel.cosbench.config.Config;
import com.intel.cosbench.log.Logger;

/**
 * This is an mocked storage, which just inserts a short delay before return for
 * each request.
 * 
 * @author ywang19, qzheng7
 * 
 */
class MockStorage extends NoneStorage {

    private Random random = new Random();
    private Statistics stats = new Statistics();

    /* configurations */
    private long size; // object size (in bytes)
    private long delay; // operation delay (in milliseconds)
    private double errors; // error rate for error injection [0,1]
    private boolean printing; // enable printing object content (for debugging)
    private boolean profiling; // enable operation profiling (for debugging)

    /* current working thread */
    private volatile Thread thread;

    public MockStorage() {
        /* empty */
    }

    @Override
    public void init(Config config, Logger logger) {
        super.init(config, logger);

        stats.addProfile("GET");
        stats.addProfile("PUT");
        stats.addProfile("DEL");
        logger.debug("op profiling data has been initialized");
        
        size = config.getLong(OBJECT_SIZE_KEY, OBJECT_SIZE_DEFAULT);
        delay = config.getLong(OP_DELAY_KEY, OP_DELAY_DEFAULT);
        errors = config.getDouble(OP_ERRORS_KEY, OP_ERRORS_DEFAULT);
        printing = config.getBoolean(PRINTING_KEY, PRINTING_DEFAULT);
        profiling = config.getBoolean(PROFILING_KEY, PROFILING_DEFAULT);

        parms.put(OBJECT_SIZE_KEY, size);
        parms.put(OP_DELAY_KEY, delay);
        parms.put(OP_ERRORS_KEY, errors);
        parms.put(PRINTING_KEY, printing);
        parms.put(PROFILING_KEY, profiling);

        logger.debug("using storage config: {}", parms);
        
        logger.debug("mock client has been initialized");
    }

    @Override
    public void dispose() {
        super.dispose();
        if (profiling && logger.isDebugEnabled())
            stats.printStats(logger);
        thread = null;
    }

    @Override
    public void abort() {
        super.abort();
        if (thread != null)
            thread.interrupt();
        thread = null;
    }

    @Override
    public InputStream getObject(String container, String object, Config config) {
        super.getObject(container, object, config);
        if (profiling && logger.isDebugEnabled())
            stats.addEvent("GET", container + "/" + object);
        if (random.nextDouble() < errors)
            throw new StorageException("error injection");
        thread = Thread.currentThread();
        MockUtils.sleep(delay);
        return new NullInputStream(size);
    }
    
    @Override
    public InputStream getList(String container, String object, Config config) {
        super.getList(container, object, config);
        if (profiling && logger.isDebugEnabled())
            stats.addEvent("LIST", container + "/" + object); //###
        if (random.nextDouble() < errors)
            throw new StorageException("error injection");
        thread = Thread.currentThread();
        MockUtils.sleep(delay);
        return new NullInputStream(0); //###
    }

    @Override
    public void createContainer(String container, Config config) {
        super.createContainer(container, config);
        if (random.nextDouble() < errors)
            throw new StorageException("error injection");
        thread = Thread.currentThread();
        MockUtils.sleep(delay);
    }

    @Deprecated
    public void createObject(String container, String object, byte[] data,
            Config config) {
        super.createObject(container, object, data, config);
        if (profiling && logger.isDebugEnabled())
            stats.addEvent("PUT", container + "/" + object);
        if (random.nextDouble() < errors)
            throw new StorageException("error injection");
        if (printing)
            logger.debug("content to upload: " + MockUtils.toString(data));
        thread = Thread.currentThread();
        MockUtils.sleep(delay);
    }

    @Override
    public void createObject(String container, String object, InputStream data,
            long length, Config config) {
        super.createObject(container, object, data, length, config);
        if (profiling && logger.isDebugEnabled())
            stats.addEvent("PUT", container + "/" + object);
        if (random.nextDouble() < errors)
            throw new StorageException("error injection");
        if (printing)
            logger.debug("content to upload: " + MockUtils.toString(data));
        else
            MockUtils.consume(data);
        thread = Thread.currentThread();
        MockUtils.sleep(delay);
    }

    @Override
    public void deleteContainer(String container, Config config) {
        super.deleteContainer(container, config);
        if (random.nextDouble() < errors)
            throw new StorageException("error injection");
        thread = Thread.currentThread();
        MockUtils.sleep(delay);
    }

    @Override
    public void deleteObject(String container, String object, Config config) {
        super.deleteObject(container, object, config);
        if (profiling && logger.isDebugEnabled())
            stats.addEvent("DEL", container + "/" + object);
        if (random.nextDouble() < errors)
            throw new StorageException("error injection");
        thread = Thread.currentThread();
        MockUtils.sleep(delay);
    }

}
