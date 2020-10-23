/**

Copyright 2013 Intel Corporation, All Rights Reserved.
Copyright 2019 OpenIO Corporation, All Rights Reserved.

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

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.input.CountingInputStream;

/**
 * This class is to record the time of data transfer
 *
 *
 */

public class XferCountingInputStream extends CountingInputStream{
    private long xferStart = 0L;
    private long xferEnd = 0L;
    private boolean isFirstByte = true;

    public XferCountingInputStream(InputStream in) {
        super(in);
    }

    @Override
    public int read() throws IOException {
        int result = super.read();
        recordTime();
        return result;
    }

    @Override
    public int read(byte[] b) throws IOException {
        int result = super.read(b);
        recordTime();
        return result;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int result = super.read(b, off, len);
        recordTime();
        return result;
    }

    private void recordTime() {
        if (this.isFirstByte) {
            this.xferStart = System.nanoTime();
            this.isFirstByte = false;
        }
        this.xferEnd = System.nanoTime();
    }

    public long getXferTime() {
        long xferTime = (this.xferEnd - this.xferStart) / 1000000;
        return xferTime > 0 ? xferTime : 0L;
    }

}
