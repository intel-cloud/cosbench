package com.intel.cosbench.api.AzureBlobStor;

import com.intel.cosbench.api.storage.*;

public class AzureBlobStorageFactory implements StorageAPIFactory {
    @Override
    public String getStorageName() {
        return "azure-blob-storage";
    }

    @Override
    public StorageAPI getStorageAPI() {
        return new AzureBlobStorage();
    }
}
