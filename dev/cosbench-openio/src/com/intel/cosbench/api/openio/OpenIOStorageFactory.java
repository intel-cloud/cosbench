package com.intel.cosbench.api.openio;

import com.intel.cosbench.api.storage.StorageAPI;
import com.intel.cosbench.api.storage.StorageAPIFactory;

/**
 * 
 * @author Christopher Dedeurwaerder
 *
 */
public class OpenIOStorageFactory implements StorageAPIFactory {

    private static final String OIO_STORAGE_NAME = "openio";
    
    @Override
    public String getStorageName() {
        return OIO_STORAGE_NAME;
    }

    @Override
    public StorageAPI getStorageAPI() {
        return new OpenIOStorage();
    }

}
