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

package com.intel.cosbench.api.mock;

import java.io.*;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.NullOutputStream;

import com.intel.cosbench.api.storage.StorageInterruptedException;

class MockUtils {

    public static void sleep(long delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException ie) {
            throw new StorageInterruptedException(ie);
        }
    }

    public static void consume(InputStream data) {
        try {
            IOUtils.copyLarge(data, new NullOutputStream());
        } catch (IOException ioe) {
            // will not happen
        }
    }

    public static String toString(InputStream data) {
        String string = null;
        try {
            string = IOUtils.toString(data);
        } catch (IOException ioe) {
            // will not happen
        }
        return string;
    }

    public static String toString(byte[] data) {
        String string = null;
        try {
            string = new String(data, "US-ASCII");
        } catch (UnsupportedEncodingException uee) {
            // will not happen
        }
        return string;
    }

}
