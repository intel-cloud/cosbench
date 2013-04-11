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

package com.intel.cosbench.service;

import java.io.*;

import com.intel.cosbench.config.*;
import com.intel.cosbench.config.common.INIConfigParser;
import com.intel.cosbench.log.*;

/**
 * The class abstracts basic methods need for cosbench service.
 *  
 * @author ywang19, qzheng7
 *
 */
public abstract class AbstractServiceFactory {

    private static Logger logger = LogFactory.getSystemLogger();

    protected Config config;

    protected abstract String getConfigFile();

    protected abstract String getServiceName();

    public AbstractServiceFactory() {
        /* empty */
    }

    public void init() throws Exception {
        String path = getConfigFile();
        File file = new File(path);
        if (file.exists())
            loadConfig(file);
        else
            config = INIConfigParser.getEmptyConfig();
        initLogSystem();
    }

    private void loadConfig(File file) {
        String service = getServiceName();
        try {
            config = INIConfigParser.parse(file);
        } catch (ConfigException e) {
            String msg = "cannot load " + service + " configuration";
            throw new ConfigException(msg, e);
        }
        String path = file.getAbsolutePath();
        logger.info("use " + service + " configuration defined in {}", path);
    }

    protected void initLogSystem() throws IOException {
        LogManager manager = LogFactory.getSystemLogManager();
        String level = loadLogLevel();
        manager.setLogLevel(LogLevel.parseLevel(level));
        String filename = loadLogFile();
        File file = new File(filename);
        File dir = file.getParentFile();
        if (!dir.exists())
            dir.mkdirs();
        boolean append = true;
        boolean buffer = false;
        manager.setLogFile(dir, file.getName(), append, buffer);
    }

    protected String loadLogLevel() {
        return config.get("log_level", "INFO");
    }

    protected String loadLogFile() {
        return config.get("log_file", "log/system.log");
    }

}
