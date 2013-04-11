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
import org.slf4j.helpers.MessageFormatter;

import com.intel.cosbench.log.Logger;

/**
 * The wrapper of log4j logger.
 * 
 * @author ywang19, qzheng7
 *
 */
public class Log4jLogger implements Logger {

    private static final String FQCN = Log4jLogger.class.getName();

    private org.apache.log4j.Logger logger;

    Log4jLogger(org.apache.log4j.Logger logger) {
        this.logger = logger;
    }

    @Override
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    @Override
    public void trace(String msg) {
        logger.log(FQCN, Level.TRACE, msg, null);
    }

    @Override
    public void trace(String msg, Throwable t) {
        logger.log(FQCN, Level.TRACE, msg, t);
    }

    @Override
    public void trace(String format, Object arg) {
        if (isTraceEnabled()) {
            String msg = MessageFormatter.format(format, arg);
            logger.log(FQCN, Level.TRACE, msg, null);
        }
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        if (isTraceEnabled()) {
            String msg = MessageFormatter.format(format, arg1, arg2);
            logger.log(FQCN, Level.TRACE, msg, null);
        }
    }

    @Override
    public void trace(String format, Object[] argArray) {
        if (isTraceEnabled()) {
            String msg = MessageFormatter.format(format, argArray);
            logger.log(FQCN, Level.TRACE, msg, null);
        }
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public void debug(String msg) {
        logger.log(FQCN, Level.DEBUG, msg, null);
    }

    @Override
    public void debug(String msg, Throwable t) {
        logger.log(FQCN, Level.DEBUG, msg, t);
    }

    @Override
    public void debug(String format, Object arg) {
        if (isDebugEnabled()) {
            String msg = MessageFormatter.format(format, arg);
            logger.log(FQCN, Level.DEBUG, msg, null);
        }
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        if (isDebugEnabled()) {
            String msg = MessageFormatter.format(format, arg1, arg2);
            logger.log(FQCN, Level.DEBUG, msg, null);
        }
    }

    @Override
    public void debug(String format, Object[] argArray) {
        if (isDebugEnabled()) {
            String msg = MessageFormatter.format(format, argArray);
            logger.log(FQCN, Level.DEBUG, msg, null);
        }
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    @Override
    public void info(String msg) {
        logger.log(FQCN, Level.INFO, msg, null);
    }

    @Override
    public void info(String msg, Throwable t) {
        logger.log(FQCN, Level.INFO, msg, t);
    }

    @Override
    public void info(String format, Object arg) {
        if (isInfoEnabled()) {
            String msg = MessageFormatter.format(format, arg);
            logger.log(FQCN, Level.INFO, msg, null);
        }
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        if (isInfoEnabled()) {
            String msg = MessageFormatter.format(format, arg1, arg2);
            logger.log(FQCN, Level.INFO, msg, null);
        }
    }

    @Override
    public void info(String format, Object[] argArray) {
        if (isInfoEnabled()) {
            String msg = MessageFormatter.format(format, argArray);
            logger.log(FQCN, Level.INFO, msg, null);
        }
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isEnabledFor(Level.WARN);
    }

    @Override
    public void warn(String msg) {
        logger.log(FQCN, Level.WARN, msg, null);
    }

    @Override
    public void warn(String msg, Throwable t) {
        logger.log(FQCN, Level.WARN, msg, t);
    }

    @Override
    public void warn(String format, Object arg) {
        String msg = MessageFormatter.format(format, arg);
        logger.log(FQCN, Level.WARN, msg, null);
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        String msg = MessageFormatter.format(format, arg1, arg2);
        logger.log(FQCN, Level.WARN, msg, null);
    }

    @Override
    public void warn(String format, Object[] argArray) {
        String msg = MessageFormatter.format(format, argArray);
        logger.log(FQCN, Level.WARN, msg, null);
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isEnabledFor(Level.ERROR);
    }

    @Override
    public void error(String msg) {
        logger.log(FQCN, Level.ERROR, msg, null);
    }

    @Override
    public void error(String msg, Throwable t) {
        logger.log(FQCN, Level.ERROR, msg, t);
    }

    @Override
    public void error(String format, Object arg) {
        String msg = MessageFormatter.format(format, arg);
        logger.log(FQCN, Level.ERROR, msg, null);
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        String msg = MessageFormatter.format(format, arg1, arg2);
        logger.log(FQCN, Level.ERROR, msg, null);
    }

    @Override
    public void error(String format, Object[] argArray) {
        String msg = MessageFormatter.format(format, argArray);
        logger.log(FQCN, Level.ERROR, msg, null);
    }

}
