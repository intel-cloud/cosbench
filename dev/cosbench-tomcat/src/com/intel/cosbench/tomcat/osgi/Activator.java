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

package com.intel.cosbench.tomcat.osgi;

import org.osgi.framework.*;

import com.intel.cosbench.log.*;
import com.intel.cosbench.tomcat.*;

/**
 * The bundle activator for tomcat.
 * 
 * @author ywang19, qzheng7
 * 
 */
public class Activator implements BundleActivator {

    private static final Logger LOGGER = LogFactory.getSystemLogger();

    private BundleContext context;

    private TomcatLoader loader;

    private TomcatRegister register;

    /**
     * the entry point to start up one bundle.
     * <p>
     * This method will find configuration file following below order:
     * <ol>
     * <li>java command line option "-Dcosbench.tomcat.config";</li>
     * <li>"tomcat-server.xml" in current folder;</li>
     * <li>"tomcat-server.xml" in "conf" folder;</li>
     * <li>predefined default configuration file:
     * <ul>
     * <li>on unix like environment, it's "/etc/cosbench/tomcat-server.xml";</li>
     * <li>on windows environment, it's "c:\\tomcat-server.xml".</li>
     * </ul>
     * </li>
     * </ol>
     * 
     * @return the path to tomcat configuration file
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        this.context = context;
        Thread curr = Thread.currentThread();
        ClassLoader backup = curr.getContextClassLoader();
        try {
            curr.setContextClassLoader(Activator.class.getClassLoader());
            doStart();
        } catch (Exception e) {
            LOGGER.error("fail to start and publish tomcat service", e);
            throw new RuntimeException(e);
        } finally {
            curr.setContextClassLoader(backup);
        }
        LOGGER.info("tomcat service has been started and published");
    }

    private void doStart() {
        loader = new TomcatLoader();
        loader.start();
        register = new TomcatRegister();
        register.publishService(context, loader.getService());
    }

    public void stop(BundleContext context) throws Exception {
        Thread curr = Thread.currentThread();
        ClassLoader backup = curr.getContextClassLoader();
        try {
            curr.setContextClassLoader(Activator.class.getClassLoader());
            doStop();
        } catch (Exception e) {
            LOGGER.error("fail to unpublish and stop tomcat server", e);
            throw new RuntimeException(e);
        } finally {
            curr.setContextClassLoader(backup);
        }
        LOGGER.info("tomcat service has been unpublished and stopped");
    }

    private void doStop() {
        register.unpublishService();
        register = null;
        loader.stop();
        loader = null;
        context = null;
    }

}
