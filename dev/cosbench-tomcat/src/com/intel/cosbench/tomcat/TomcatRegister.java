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

package com.intel.cosbench.tomcat;

import java.util.*;

import javax.management.MBeanRegistration;

import org.apache.catalina.*;
import org.apache.catalina.core.StandardService;
import org.osgi.framework.*;

/**
 * The class encapsulates the registration of service exposed by Tomcat bundle.
 * 
 * @author ywang19, qzheng7
 * 
 */
public class TomcatRegister {

    private static final String[] REG_TYPES = new String[] {
            StandardService.class.getName(), Service.class.getName(),
            MBeanRegistration.class.getName(), Lifecycle.class.getName() };

    private ServiceRegistration<?> registration;

    public TomcatRegister() {
        /* empty */
    }

    /**
     * This method will register service into osgi context, so other bundles can
     * access the service.
     * 
     * @param context
     *            the osgi bundle context
     * @param service
     *            the service to be published in context
     */
    public void publishService(BundleContext context, StandardService service) {
        Dictionary<String, ?> props = new Hashtable<String, Object>();
        registration = context.registerService(REG_TYPES, service, props);
    }

    /**
     * This method will unregister service from osgi context.
     * 
     */
    public void unpublishService() {
        registration.unregister();
    }

}
