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

package com.intel.cosbench.driver.generator;

import java.security.NoSuchAlgorithmException;
import java.util.Random;

import org.apache.commons.io.input.NullInputStream;
import org.apache.commons.lang.math.RandomUtils;

import com.intel.cosbench.driver.util.HashUtil;
import com.intel.cosbench.log.*;

/**
 * This class is to generate random data as input stream for data uploading.
 * 
 * @author ywang19, qzheng7
 * 
 */
public class RandomInputStream extends NullInputStream {

    private static final int SIZE = 4096; // 4 KB

    private byte[] buffer;

    private boolean hashCheck = false;
    private HashUtil util = null;
    private int hashLen = 0;
    private byte[] hashBytes;
    private long size = 0;
    private long processed = 0;

    private static Logger logger = LogFactory.getSystemLogger();

    public RandomInputStream(long size, Random random, boolean isRandom,
            boolean hashCheck) {
        super(size);

        this.hashCheck = hashCheck;
        try {
            this.util = new HashUtil();
        } catch (NoSuchAlgorithmException e) {
            logger.error("Alogrithm not found", e);
        }
        this.hashLen = this.util.getHashLen();
        if (size <= hashLen) {
            logger.warn("The size is too small to embed checksum, will ignore integrity checking.");
            this.hashCheck = false;
            this.util = null;
            this.hashLen = 0;
        }
        this.size = size;

        buffer = new byte[SIZE];
        if (isRandom)
            for (int i = 0; i < SIZE; i++)
                buffer[i] = (byte) (RandomUtils.nextInt(random, 26) + 'a');
    }

    @Override
    protected int processByte() {
        throw new UnsupportedOperationException("do not read byte by byte");
    }

    @Override
    protected void processBytes(byte[] bytes, int offset, int length) {

        if (!hashCheck) {
            do {
                int segment = length > SIZE ? SIZE : length;
                System.arraycopy(buffer, 0, bytes, offset, segment);

                length -= segment;
                offset += segment;
            } while (length > 0); // data copy completed

        } else {
            if (length <= hashLen) {
                System.arraycopy(hashBytes, hashLen - length, bytes, 0, length);

                return;
            }

            int gap = (int) ((processed + length) - (size - hashLen));
            if (gap > 0) // partial hash needs append in gap area.
                length -= gap;

            processed += length;
            do {
                int segment = length > SIZE ? SIZE : length;
                System.arraycopy(buffer, 0, bytes, offset, segment);
                util.update(buffer, 0, segment);

                length -= segment;
                offset += segment;
            } while (length > 0); // data copy completed

            if ((gap <= hashLen) && (gap >= 0)) {
                // append md5 hash
                String hashString = util.calculateHash();

                try {
                    hashBytes = hashString.getBytes("UTF-8");
                } catch (Exception e) {
                    e.printStackTrace();
                    hashBytes = hashString.getBytes();
                }

                if (gap > 0)
                    System.arraycopy(hashBytes, 0, bytes, offset, gap);
            }

        }
    }

}
