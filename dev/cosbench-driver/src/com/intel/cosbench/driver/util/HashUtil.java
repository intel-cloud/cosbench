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

import java.math.BigInteger;
import java.security.*;

public class HashUtil {

    private MessageDigest algo = null;
    public static final String GUARD = "!!!!";

    public HashUtil() throws NoSuchAlgorithmException {
        this("MD5");
    }

    public HashUtil(String _algo) throws NoSuchAlgorithmException {
        algo = MessageDigest.getInstance(_algo);
        algo.reset();
    }

    public int getHashLen() {
        return algo.getDigestLength() * 2 + GUARD.length() * 2;
    }

    public void update(byte[] data, int offset, int length) {
        algo.update(data, offset, length);
    }

    public void update(byte[] data) {
        this.update(data, 0, data.length);
    }

    public String calculateHash() {
        // append md5 hash
        byte[] hash = algo.digest();
        BigInteger bi = new BigInteger(1, hash);
        String hexHash = String.format("%1$032x", bi);

        return GUARD + hexHash + GUARD;
    }

}
