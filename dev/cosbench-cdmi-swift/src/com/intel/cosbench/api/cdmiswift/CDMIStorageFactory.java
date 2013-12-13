package com.intel.cosbench.api.cdmiswift;

import com.intel.cosbench.api.storage.*;

public class CDMIStorageFactory implements StorageAPIFactory {

    @Override
    public String getStorageName() {
        return "cdmi_swift";
    }

    @Override
    public StorageAPI getStorageAPI() {
        return new CDMIStorage();
    }

}
