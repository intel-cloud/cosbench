package com.intel.cosbench.api.QiniuStor;

import com.intel.cosbench.api.storage.*;

public class QiniuStorageFactory implements StorageAPIFactory {

    @Override
    public String getStorageName() {
        return "qiniu";
    }

    @Override
    public StorageAPI getStorageAPI() {
        return new QiniuStorage();
    }

}
