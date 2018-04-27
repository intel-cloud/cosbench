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

package com.intel.cosbench.controller.service;

import static org.apache.commons.lang.SystemUtils.IS_OS_WINDOWS;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.channels.ReadableByteChannel;

import com.intel.cosbench.config.castor.CastorConfigTools;
import com.intel.cosbench.controller.model.*;
import com.intel.cosbench.service.*;

public class COSBControllerServiceFactory extends AbstractServiceFactory
        implements ControllerServiceFactory {

    private static final String SERVICE_NAME = "controller";

    private static final String CFG_FILE_KEY = "cosbench.controller.config";

    private static final String UNIX_DEFAULT_CFG_FILE = "/etc/cosbench/controller.conf";

    private static final String WIN_DEFAULT_CFG_FILE = "C:\\controller.conf";

    public COSBControllerServiceFactory() {
        /* loading workload XML mappings */
        CastorConfigTools.getWorkloadResolver();
//        /* creating workload archive directory */
//        new SimpleWorkloadArchiver();
    }

    @Override
    protected String getConfigFile() {
        String configFile;
        if ((configFile = System.getProperty(CFG_FILE_KEY)) != null)
            return configFile;
        if (new File("controller.conf").exists())
            return "controller.conf";
        if (new File("conf/controller.conf").exists())
            return "conf/controller.conf";
        return IS_OS_WINDOWS ? WIN_DEFAULT_CFG_FILE : UNIX_DEFAULT_CFG_FILE;
    }

    @Override
    protected String getServiceName() {
        return SERVICE_NAME;
    }

    @Override
    public ControllerService getControllerService() {
        COSBControllerService service = new COSBControllerService();
        ControllerContext context = getControllerContext();
        service.setContext(context);
        service.init();
        return service;
    }

    private ControllerContext getControllerContext() {
        ControllerContext context = new ControllerContext();
        context.setName(loadControllerName());
        context.setUrl(loadControllerUrl());
        context.setArchive_dir(loadArchiveDir());
        context.setConcurrency(loadConcurrency());
        context.setDriverRegistry(getDriverRegistry());
        context.setVersion(getVersion());
        return context;
    }
    
    private String getVersion() {
		// TODO Auto-generated method stub
    	String str = getName("VERSION");
    	String str2 = getName("BUILD.no");
    	return str+"."+str2;
	}
    
    private String getName(String fileName){
    	 String str = null ;
         File myFile=new File(fileName);
         if(!myFile.exists()){ 
             System.err.println("Can't Find " + fileName);
         }
         try {
             BufferedReader in = new BufferedReader(new FileReader(myFile));
             str = in.readLine();
             in.close();
         } 
         catch (IOException e) {
             e.getStackTrace();
         }
 		return str;
    }
    
	protected String loadLogLevel() {
        return config.get("controller.log_level", "INFO");
    }

    protected String loadLogFile() {
        return config.get("controller.log_file", "log/system.log");
    }

    private String loadArchiveDir() {
    	return config.get("controller.archive_dir", "archive");
    }
    
    private String loadControllerName() {
        return config.get("controller.name", "N/A");
    }

    private String loadControllerUrl() {
        return config.get("controller.url", "N/A");
    }

    private int loadConcurrency() {
        return config.getInt("controller.concurrency", 1);
    }

    private DriverRegistry getDriverRegistry() {
        DriverRegistry registry = new DriverRegistry();
        int drivers = config.getInt("controller.drivers");
        for (int i = 1; i <= drivers; i++) {
            DriverContext context = getDriverContext(i);
            registry.addDriver(context);
        }
        return registry;
    }

    private DriverContext getDriverContext(int index) {
        DriverContext context = new DriverContext();
        context.setName(loadDriverName(index));
        context.setUrl(loadDriverUrl(index));
        context.setAliveState(false);
        return context;
    }

    private String loadDriverName(int index) {
        return config.get("driver" + index + ".name");
    }

    private String loadDriverUrl(int index) {
        return config.get("driver" + index + ".url");
    }

}
