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

import org.apache.commons.configuration.*;
import org.apache.commons.lang.StringUtils;

import com.intel.cosbench.config.Config;
import com.intel.cosbench.log.*;

/**
 * The Parser for key-value configuration.
 * 
 * @author ywang19, qzheng7
 *
 */
public class KVConfigParser {

    private static Logger logger = LogFactory.getSystemLogger();

    public static Config parse(String str) {
        BaseConfiguration config = new BaseConfiguration();
        config.setDelimiterParsingDisabled(true);
        str = StringUtils.trimToEmpty(str);
        String[] entries = StringUtils.split(str, ';');
        for (String entry : entries) {
            addConfigEntry(entry, config);
        }
        return new COSBConfigApator(config);
    }

    private static void addConfigEntry(String entry, Configuration config) {
        int pos = StringUtils.indexOf(entry, '=');
        if(pos < 0)
            logger.warn("cannot parse config entry {}", entry);
        
        String key = StringUtils.trim(StringUtils.left(entry,  pos));
        String value = StringUtils.trim(StringUtils.right(entry,  entry.length() - pos -1));
        logger.debug("key=" + key + ";value=" + value);

        config.setProperty(key, value);
    }
}
