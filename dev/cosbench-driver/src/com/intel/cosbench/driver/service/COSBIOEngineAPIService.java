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

import com.intel.cosbench.api.ioengine.*;
import com.intel.cosbench.config.*;
import com.intel.cosbench.log.*;

/**
 * This class is the major service for IOEngineentication on driver.
 * 
 * @IOEngineor ywang19
 * 
 */
public class COSBIOEngineAPIService implements IOEngineAPIService {

    private static final Logger LOGGER = LogFactory.getSystemLogger();

    private List<IOEngineAPIFactory> factories;

    public COSBIOEngineAPIService() {
        /* empty */
    }

    public void setFactories(List<IOEngineAPIFactory> factories) {
        this.factories = factories;
        StringBuilder buffer = new StringBuilder();
        for (IOEngineAPIFactory factory : factories)
            buffer.append(factory.getName()).append(", ");
        buffer.delete(buffer.length() - 2, buffer.length());
        LOGGER.info("detected supported IOEngine types: {}", buffer);
    }

    @Override
    public IOEngineAPI getIOEngine(String type, Config config, Logger logger) {
        IOEngineAPI IOEngine = createIOEngine(type);
        IOEngine.init(config, logger);
        return IOEngine;
    }

    private IOEngineAPI createIOEngine(String type) {
        if (NoneIOEngine.API_TYPE.equals(type))
            return new NoneIOEngine();
        for (IOEngineAPIFactory factory : factories)
            if (factory.getName().equals(type))
                return factory.getAPI();
        String msg = "unrecognized IOEngine type: " + type;
        throw new ConfigException(msg);
    }

}
