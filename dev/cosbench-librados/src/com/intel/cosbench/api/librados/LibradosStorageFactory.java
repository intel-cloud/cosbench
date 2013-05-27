/*
 * LibradosStorageFactory.java 24.05.2013
 * 
 * Copyright (c) 2013 1&1 Internet AG. All rights reserved.
 * 
 * @author: Niklas Goerke - niklas974@github
 */
package com.intel.cosbench.api.librados;

import com.intel.cosbench.api.storage.*;

public class LibradosStorageFactory implements StorageAPIFactory {

    @Override
    public String getStorageName() {
        return "librados";
    }

    @Override
    public StorageAPI getStorageAPI() {
        return new LibradosStorage();
    }

}
