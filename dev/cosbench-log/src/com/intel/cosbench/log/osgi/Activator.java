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

package com.intel.cosbench.log.osgi;

import org.osgi.framework.*;

import com.intel.cosbench.log.*;


/**
 * The bundle activator for cosbench log.
 * cosbench log bundle includes a few interfaces, and one log4j based implementation.
 * 
 * @author ywang19, qzheng7
 *
 */
public class Activator implements BundleActivator {

    public void start(BundleContext bundleContext) throws Exception {
        LogManager manager = LogFactory.getSystemLogManager();
        String console = System.getProperty("cosbench.log.console");
        if (Boolean.parseBoolean(console))
            manager.enableConsole();
    }

    public void stop(BundleContext bundleContext) throws Exception {
        LogManager manager = LogFactory.getSystemLogManager();
        manager.dispose();
    }

}
