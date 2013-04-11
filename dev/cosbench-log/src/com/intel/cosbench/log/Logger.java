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

package com.intel.cosbench.log;

/**
 * The interface of Logger.
 * 
 * @author ywang19, qzheng7
 *
 */
public interface Logger {

    public boolean isTraceEnabled();

    public void trace(String msg);

    public void trace(String msg, Throwable t);

    public void trace(String format, Object arg);

    public void trace(String format, Object arg1, Object arg2);

    public void trace(String format, Object[] argArray);

    public boolean isDebugEnabled();

    public void debug(String msg);

    public void debug(String msg, Throwable t);

    public void debug(String format, Object arg);

    public void debug(String format, Object arg1, Object arg2);

    public void debug(String format, Object[] argArray);

    public boolean isInfoEnabled();

    public void info(String msg);

    public void info(String msg, Throwable t);

    public void info(String format, Object arg);

    public void info(String format, Object arg1, Object arg2);

    public void info(String format, Object[] argArray);

    public boolean isWarnEnabled();

    public void warn(String msg);

    public void warn(String msg, Throwable t);

    public void warn(String format, Object arg);

    public void warn(String format, Object arg1, Object arg2);

    public void warn(String format, Object[] argArray);

    public boolean isErrorEnabled();

    public void error(String msg);

    public void error(String msg, Throwable t);

    public void error(String format, Object arg);

    public void error(String format, Object arg1, Object arg2);

    public void error(String format, Object[] argArray);

}
