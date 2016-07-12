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

import com.intel.cosbench.api.auth.NoneAuth;
import com.intel.cosbench.api.context.AuthContext;
import com.intel.cosbench.api.context.DefaultAuthContext;
import com.intel.cosbench.config.Config;
import com.intel.cosbench.log.Logger;

/**
 * This is an mocked authentication mechanism, which just inserts a short delay
 * before return for login request.
 * 
 * @author ywang19, qzheng7
 * 
 */
class MockAuth extends NoneAuth {

    /* configurations */
    private String token; // user token
    private long delay; // in milliseconds

    public MockAuth() {
        /* empty */
    }

    @Override
    public void init(Config config, Logger logger) {
        super.init(config, logger);

        token = config.get(AUTH_TOKEN_KEY, AUTH_TOKEN_DEFAULT);
        delay = config.getLong(OP_DELAY_KEY, OP_DELAY_DEFAULT);

        parms.put(AUTH_TOKEN_KEY, token);
        parms.put(OP_DELAY_KEY, delay);

        logger.debug("using auth config: {}", parms);
        
        logger.debug("mock auth has been initialized");
    }

    @Override
    public AuthContext login() {
        super.login();
        MockUtils.sleep(delay);
        AuthContext context = new DefaultAuthContext();
        context.put(AUTH_TOKEN_KEY, token);
        return context;
    }

}
