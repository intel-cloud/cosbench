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

package com.intel.cosbench.log.log4j;

import java.io.*;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.*;
import org.apache.log4j.spi.RootLogger;
import org.apache.log4j.varia.NullAppender;

import com.intel.cosbench.log.*;
import com.intel.cosbench.log.LogManager;
import com.intel.cosbench.log.Logger;

/**
 * The wrapper of log4j LogManager; this log manager will create log file with
 * maximum size of 10 MB, and the maximum number of backup file is 10. The
 * default log level is INFO.
 * 
 * @author ywang19, qzheng7
 * 
 */
public class Log4jLogManager implements LogManager {

    private static final int FILE_NUM = 10;

    private static final int FILE_SIZE = 10 * 1024 * 1024;

    private static final int BUFFER_SIZE = 10 * 1024;

    private File file; /* log file */
    private Logger logger; /* root logger */
    private Hierarchy repository; /* logger repository */

    private boolean fileAttached = false;
    private boolean consoleEnabled = false;

    public Log4jLogManager() {
        Level level = Level.INFO;
        org.apache.log4j.Logger root = new RootLogger(level);
        repository = new Hierarchy(root);
        root.addAppender(new NullAppender());
        logger = new Log4jLogger(root);
    }

    @Override
    public void dispose() {
        repository.shutdown();
        repository = null;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public LogLevel getLogLevel() {
        Level level = repository.getRootLogger().getLevel();
        return Log4jUtils.fromLog4jLevel(level);
    }

    @Override
    public void setLogLevel(LogLevel level) {
        Level newLevel = Log4jUtils.toLog4jLevel(level);
        repository.getRootLogger().setLevel(newLevel);
        this.logger.debug("log level has been set to {}", level.name());
    }

    @Override
    public synchronized void setLogFile(File dir, String filename,
            boolean append, boolean buffer) throws IOException {
        if (!fileAttached) {
            file = new File(dir, filename);
            filename = file.getAbsolutePath();
            org.apache.log4j.Logger logger = repository.getRootLogger();
            FileAppender appender = createFileAppender();
            appender.setFile(filename, append, buffer, BUFFER_SIZE);
            logger.addAppender(appender);
            fileAttached = true;
            String path = file.getAbsolutePath();
            this.logger.info("will append log to file {}", path);
        } else
            this.logger.debug("attempt to attach multiple log files");
    }

    private static FileAppender createFileAppender() {
        RollingFileAppender appender = new RollingFileAppender();
        appender.setName("FILE");
        appender.setLayout(new PatternLayout("%d [%p] [%C{1}] - %m%n"));
        appender.setMaximumFileSize(FILE_SIZE);
        appender.setMaxBackupIndex(FILE_NUM);
        return appender;
    }

    @Override
    public synchronized void enableConsole() {
        if (!consoleEnabled) {
            org.apache.log4j.Logger logger = repository.getRootLogger();
            ConsoleAppender appender = createConsoleAppender();
            logger.addAppender(appender);
            consoleEnabled = true;
            this.logger.info("will append log to console");
        } else
            this.logger.debug("attempt to attach multiple consoles");
    }

    private static ConsoleAppender createConsoleAppender() {
        String target = ConsoleAppender.SYSTEM_OUT;
        Layout layout = new PatternLayout(
                "(%F:%L)%n%d [%-15.15t] [%-5p] - %m%n");
        ConsoleAppender appender = new ConsoleAppender(layout, target);
        appender.setName("CONSOLE");
        return appender;
    }

    @Override
    public String getLogAsString() throws IOException {
        return FileUtils.readFileToString(file);
    }

}
