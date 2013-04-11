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

import com.intel.cosbench.api.auth.*;
import com.intel.cosbench.config.*;
import com.intel.cosbench.log.*;

/**
 * This class is the major service for authentication on driver.
 * 
 * @author ywang19, qzheng7
 * 
 */
public class COSBAuthAPIService implements AuthAPIService {

    private static final Logger LOGGER = LogFactory.getSystemLogger();

    private List<AuthAPIFactory> factories;

    public COSBAuthAPIService() {
        /* empty */
    }

    public void setFactories(List<AuthAPIFactory> factories) {
        this.factories = factories;
        StringBuilder buffer = new StringBuilder();
        for (AuthAPIFactory factory : factories)
            buffer.append(factory.getAuthName()).append(", ");
        buffer.delete(buffer.length() - 2, buffer.length());
        LOGGER.info("detected supported auth types: {}", buffer);
    }

    @Override
    public AuthAPI getAuth(String type, Config config, Logger logger) {
        AuthAPI auth = createAuth(type);
        auth.init(config, logger);
        return auth;
    }

    private AuthAPI createAuth(String type) {
        if (NoneAuth.API_TYPE.equals(type))
            return new NoneAuth();
        for (AuthAPIFactory factory : factories)
            if (factory.getAuthName().equals(type))
                return factory.getAuthAPI();
        String msg = "unrecognized auth type: " + type;
        throw new ConfigException(msg);
    }

}
