package com.intel.cosbench.api.S3Stor;

import com.intel.cosbench.api.storage.*;

public class S3StorageFactory implements StorageAPIFactory {

    @Override
    public String getStorageName() {
        return "s3";
    }

    @Override
    public StorageAPI getStorageAPI() {
        return new S3Storage();
    }

}
