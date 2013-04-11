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

import org.apache.log4j.Level;

import com.intel.cosbench.log.LogLevel;

public class Log4jUtils {

    /**
     * Converts cosbench log level to log4j log level.
     * 
     * @param level
     *            cosbench log level
     * @return corresponding log4j log level.
     */
    public static Level toLog4jLevel(LogLevel level) {
        if (LogLevel.TRACE.equals(level))
            return Level.TRACE;
        if (LogLevel.DEBUG.equals(level))
            return Level.DEBUG;
        if (LogLevel.INFO.equals(level))
            return Level.INFO;
        if (LogLevel.WARN.equals(level))
            return Level.WARN;
        if (LogLevel.ERROR.equals(level))
            return Level.ERROR;
        String msg = "unknown log level: " + level;
        throw new IllegalStateException(msg);
    }

    /**
     * Converts log4j log level to cosbench log level
     * 
     * @param level
     *            log4j log level
     * @return corresponding cosbench log level
     */
    public static LogLevel fromLog4jLevel(Level level) {
        if (Level.TRACE.equals(level))
            return LogLevel.TRACE;
        if (Level.DEBUG.equals(level))
            return LogLevel.DEBUG;
        if (Level.INFO.equals(level))
            return LogLevel.INFO;
        if (Level.WARN.equals(level))
            return LogLevel.WARN;
        if (Level.ERROR.equals(level))
            return LogLevel.ERROR;
        String msg = "unknown log level: " + level;
        throw new IllegalStateException(msg);
    }

}
