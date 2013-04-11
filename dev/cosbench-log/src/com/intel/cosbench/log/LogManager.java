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

import java.io.*;

/**
 * The interface of LogManager.
 * 
 * @author ywang19, qzheng7
 * 
 */
public interface LogManager {

    public void dispose();

    public Logger getLogger();

    public LogLevel getLogLevel();

    public void setLogLevel(LogLevel level);

    public void setLogFile(File dir, String filename, boolean append,
            boolean buffer) throws IOException;

    public void enableConsole();

    public String getLogAsString() throws IOException;

}
