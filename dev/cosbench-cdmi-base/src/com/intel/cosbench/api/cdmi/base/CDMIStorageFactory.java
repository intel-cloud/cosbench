package com.intel.cosbench.api.cdmi.base;

import com.intel.cosbench.api.storage.*;

public class CDMIStorageFactory implements StorageAPIFactory {

    @Override
    public String getStorageName() {
        return "cdmi";
    }

    @Override
    public StorageAPI getStorageAPI() {
        return new CDMIStorage();
    }

}
