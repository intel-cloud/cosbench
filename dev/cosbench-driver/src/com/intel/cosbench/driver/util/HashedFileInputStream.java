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

package com.intel.cosbench.driver.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.input.NullInputStream;

/**
 * This class is used to concatinate the file given with its hash (hash is formatted !!!!$hash!!!!)
 * 
 * @author Niklas Goerke niklas974@github
 * 
 */
public class HashedFileInputStream extends NullInputStream {

    private static final int SIZE = 4096; // 4 KB

    private boolean hashCheck = false;
    private HashUtil util = null;
    private int hashLen = 0;
    private byte[] hashBytes;
    private int hashBytesRet = 0;
    private long size = 0;
    private FileInputStream fs = null;

    public HashedFileInputStream(File file, boolean hashCheck, HashUtil hashutil, long size) throws FileNotFoundException{
        super(size);
        if (!file.canRead()) {
            throw new FileNotFoundException("can not read from " + file.getName());
        }
        this.util = hashutil;
        this.fs = new FileInputStream(file);
        this.hashCheck = hashCheck;
        this.size = size;
        this.hashLen = this.util.getHashLen();
    }
    
    @Override
    protected int processByte() {
        throw new UnsupportedOperationException("do not read byte by byte");
    }

    @Override
    protected void processBytes(byte[] bytes, int offset, int length) {
        if (hashCheck) {
            int read = -1;
            do {
                int segment = length > SIZE ? SIZE : length;
                byte[] buffer = new byte[segment];
                try {
                    read = fs.read(buffer, 0, segment);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (read == -1) { // if we read all the buffer, calculate the hash!
                    String hashString = util.calculateHash();
                    try {
                        hashBytes = hashString.getBytes("UTF-8");
                    } catch (Exception e) {
                        e.printStackTrace();
                        hashBytes = hashString.getBytes();
                    }
                    break; // nothing to read anymore
                }
                System.arraycopy(buffer, 0, bytes, offset, read);
                util.update(buffer, 0, read);

                length -= read;
                offset += read;
            } while (length > 0); // data copy completed
            if (length > 0) { // still more bytes wanted
                int hashToGive = this.hashLen - this.hashBytesRet; // how much more of the hash to we still have to give?
                int segment = length > hashToGive ? hashToGive : length; // how much of that can we give this time?

                System.arraycopy(hashBytes, 0, bytes, offset, segment); // give that much
                this.hashBytesRet += segment; // and remember where to start next time
            }
        }
    }

    public long getSize() {
        return size;
    }

}
