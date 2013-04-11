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

package com.intel.cosbench.config.common;

import java.io.File;

import org.apache.commons.configuration.INIConfiguration;

import com.intel.cosbench.config.*;
import com.intel.cosbench.log.*;


/**
 * The parser for INI format configuration.
 * 
 * @author ywang19, qzheng7
 *
 */
public class INIConfigParser {

    private static Logger logger = LogFactory.getSystemLogger();

    public static Config parse(File file) {
        INIConfiguration config = null;
        try {
            config = new INIConfiguration(file);
        } catch (Exception e) {
            String msg = "cannot loader config from " + file.getAbsolutePath();
            logger.error(msg, e);
            throw new ConfigException(msg, e);
        }
        return new COSBConfigApator(config);
    }

    public static Config getEmptyConfig() {
        INIConfiguration config = new INIConfiguration();
        return new COSBConfigApator(config);
    }

}
