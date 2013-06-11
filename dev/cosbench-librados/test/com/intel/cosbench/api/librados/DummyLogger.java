/*
 * DummyLogger.java 28.05.2013
 * 
 * Copyright (c) 2013 1&1 Internet AG. All rights reserved.
 * 
 * $Id$
 */
package com.intel.cosbench.api.librados;

import com.intel.cosbench.log.Logger;

public class DummyLogger implements Logger {

    @Override
    public boolean isTraceEnabled() {
        return true;
    }

    @Override
    public void trace(String msg) {
    }

    @Override
    public void trace(String msg, Throwable t) {
    }

    @Override
    public void trace(String format, Object arg) {
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
    }

    @Override
    public void trace(String format, Object[] argArray) {
    }

    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    @Override
    public void debug(String msg) {
    }

    @Override
    public void debug(String msg, Throwable t) {
    }

    @Override
    public void debug(String format, Object arg) {
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
    }

    @Override
    public void debug(String format, Object[] argArray) {
    }

    @Override
    public boolean isInfoEnabled() {
        return false;
    }

    @Override
    public void info(String msg) {
    }

    @Override
    public void info(String msg, Throwable t) {
    }

    @Override
    public void info(String format, Object arg) {
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
    }

    @Override
    public void info(String format, Object[] argArray) {
    }

    @Override
    public boolean isWarnEnabled() {
        return false;
    }

    @Override
    public void warn(String msg) {
    }

    @Override
    public void warn(String msg, Throwable t) {
    }

    @Override
    public void warn(String format, Object arg) {
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
    }

    @Override
    public void warn(String format, Object[] argArray) {
    }

    @Override
    public boolean isErrorEnabled() {
        return false;
    }

    @Override
    public void error(String msg) {
    }

    @Override
    public void error(String msg, Throwable t) {
    }

    @Override
    public void error(String format, Object arg) {
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
    }

    @Override
    public void error(String format, Object[] argArray) {
    }

}
