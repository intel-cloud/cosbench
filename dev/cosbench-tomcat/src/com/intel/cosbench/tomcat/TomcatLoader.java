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

import static org.apache.commons.lang.SystemUtils.IS_OS_WINDOWS;

import java.io.File;

import org.apache.catalina.Service;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardService;
import org.apache.catalina.startup.Catalina;

import com.intel.cosbench.log.*;

/**
 * The class encapsulates tomcat configuration loading and catalina starting up.
 * 
 * @author ywang19, qzheng7
 * 
 */
public class TomcatLoader extends Catalina {

    private static final String CFG_FILE_KEY = "cosbench.tomcat.config";

    private static final String UNIX_DEFAULT_CFG_FILE = "/etc/cosbench/tomcat-server.xml";

    private static final String WIN_DEFAULT_CFG_FILE = "C:\\tomcat-server.xml";

    private static final Logger LOGGER = LogFactory.getSystemLogger();

    public TomcatLoader() {
        setName("Catalina");
        setAwait(false);
        setConfig(getServerConfig());
        setUseShutdownHook(false);
        setParentClassLoader(Thread.currentThread().getContextClassLoader());
        LOGGER.info("using tomcat configuration defined in {}", configFile);
    }

    /**
     * Returns an the path to tomcat configuration file.
     * <p>
     * This method will find configuration file following below order: 1.
     * through java command line option "-Dcosbench.tomcat.config", 2.
     * "tomcat-server.xml" in current folder, 3. "tomcat-server.xml" in conf/
     * folder, 4. predefined default configuration file: - on unix like
     * environment, it's "/etc/cosbench/tomcat-server.xml" - on windows
     * environment, it's "c:\\tomcat-server.xml"
     * 
     * @return the path to tomcat configuration file
     */
    private static String getServerConfig() {
        String configFile;
        if ((configFile = System.getProperty(CFG_FILE_KEY)) != null)
            return configFile;
        if (new File("tomcat-server.xml").exists())
            return "tomcat-server.xml";
        if (new File("conf/tomcat-server.xml").exists())
            return "conf/tomcat-server.xml";
        return IS_OS_WINDOWS ? WIN_DEFAULT_CFG_FILE : UNIX_DEFAULT_CFG_FILE;
    }

    /**
     * Start up tomcat service.
     * <p>
     * This method will start up tomcat service, and show listening port
     * information on console.
     * 
     */
    @Override
    public void start() {
        super.start();
        StandardService service = getService();
        for (Connector connector : service.findConnectors())
            printToConsole(connector.getPort());
    }

    private static void printToConsole(int port) {
        System.err.println("----------------------------------------------");
        String msg = "!!! Service will listen on web port: " + port + " !!!";
        System.err.println(msg);
        System.err.println("----------------------------------------------");
    }

    /**
     * Returns the first Standard service defined in tomcat configuration file.
     * <p>
     * This method will check tomcat configuration file, and find out the first
     * tomcat service.
     */
    public StandardService getService() {
        Service[] services = server.findServices();
        if (services == null || services.length == 0)
            throw new RuntimeException(
                    "invalid tomcat config: non service defined");
        if (services.length != 1)
            throw new RuntimeException(
                    "invalid tomcat config: multiple services");
        return (StandardService) server.findServices()[0];
    }

}
