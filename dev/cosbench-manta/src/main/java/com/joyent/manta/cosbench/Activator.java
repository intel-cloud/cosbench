/**

Copyright 2013 Intel Corporation, All Rights Reserved.
Copyright 2019 OpenIO Corporation, All Rights Reserved.

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
package com.joyent.manta.cosbench;

import com.intel.cosbench.log.LogFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Default OSGI activator that provides the startup and shutdown functionaly
 * needed to load and unload classes from OSGI.
 *
 * @author <a href="https://github.com/dekobon">Elijah Zupancic</a>
 */
public class Activator implements BundleActivator {

    /**
     * OSGI start up method.
     * @param bundleContext OSGI bundle
     * @throws Exception when there is a problem in OSGI start up
     */
    public void start(final BundleContext bundleContext) throws Exception {
        LogFactory.getSystemLogger().info("Starting Manta adapter");
    }

    /**
     * OSGI stop method.
     * @param bundleContext OSGI bundle
     * @throws Exception when there is a problem in OSGI shut down
     */
    public void stop(final BundleContext bundleContext) throws Exception {
        LogFactory.getSystemLogger().info("Stopping Manta adapter");
    }
}
