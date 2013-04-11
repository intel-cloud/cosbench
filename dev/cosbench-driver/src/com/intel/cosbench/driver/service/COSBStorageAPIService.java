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

package com.intel.cosbench.driver.service;

import java.util.List;

import com.intel.cosbench.api.storage.*;
import com.intel.cosbench.config.*;
import com.intel.cosbench.log.*;

/**
 * This class is the major service for storage on driver.
 * 
 * @author ywang19, qzheng7
 * 
 */
public class COSBStorageAPIService implements StorageAPIService {

    private static final Logger LOGGER = LogFactory.getSystemLogger();

    private List<StorageAPIFactory> factories;

    public COSBStorageAPIService() {
        /* empty */
    }

    public void setFactories(List<StorageAPIFactory> factories) {
        this.factories = factories;
        StringBuilder buffer = new StringBuilder();
        for (StorageAPIFactory factory : factories)
            buffer.append(factory.getStorageName()).append(", ");
        buffer.delete(buffer.length() - 2, buffer.length());
        LOGGER.info("detected supported storage types: {}", buffer);
    }

    @Override
    public StorageAPI getStorage(String type, Config config, Logger logger) {
        StorageAPI storage = createStorage(type);
        storage.init(config, logger);
        return storage;
    }

    private StorageAPI createStorage(String type) {
        if (NoneStorage.API_TYPE.equals(type))
            return new NoneStorage();
        for (StorageAPIFactory factory : factories)
            if (factory.getStorageName().equals(type))
                return factory.getStorageAPI();
        String msg = "unrecognized storage type: " + type;
        throw new ConfigException(msg);
    }

}
