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

package com.intel.cosbench.config.castor;

import static com.intel.cosbench.config.castor.CastorMappings.*;

import java.net.URL;
import java.util.*;

import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.xml.XMLContext;

import com.intel.cosbench.config.ConfigException;
import com.intel.cosbench.log.*;

/**
 * Base class for castor configuration.
 * 
 * @author ywang19, qzheng7
 *
 */
class CastorConfigBase {

    private static Logger logger = LogFactory.getSystemLogger();

    private static XMLContext context = createContext();

    protected XMLContext getContext() {
        return context;
    }

    private static XMLContext createContext() {
        XMLContext context = new XMLContext();
        Mapping mapping = loadMappings();
        try {
            context.addMapping(mapping);
        } catch (Exception e) {
            String msg = "cannot add the given castor mapping";
            logger.error(msg, e);
            throw new ConfigException(msg, e);
        }
        return context;
    }

    private static Mapping loadMappings() {
        Mapping mapping = new Mapping();
        for (URL url : getMappingUrls()) {
            loadMapping(mapping, url);
        }
        return mapping;
    }

    private static void loadMapping(Mapping mapping, URL url) {
        try {
            mapping.loadMapping(url);
        } catch (Exception e) {
            String msg = "cannot load the given castor mapping";
            logger.error(msg, e);
            throw new ConfigException(msg, e);
        }
        logger.debug("sucessfully loaded mapping from {}", url);
    }

    private static URL[] getMappingUrls() {
        List<URL> urls = new ArrayList<URL>();
        urls.add(AUTH);
        urls.add(STORAGE);
        urls.add(OPERATION);
        urls.add(MISSION);
        urls.add(WORK);
        urls.add(WORKSTAGE);
        urls.add(WORKFLOW);
        urls.add(WORKLOAD);
        return urls.toArray(new URL[urls.size()]);
    }
}
