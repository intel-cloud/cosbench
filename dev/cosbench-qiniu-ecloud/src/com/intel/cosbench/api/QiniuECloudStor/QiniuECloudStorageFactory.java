package com.intel.cosbench.api.QiniuECloudStor;

import com.intel.cosbench.api.storage.*;

public class QiniuECloudStorageFactory implements StorageAPIFactory {

    @Override
    public String getStorageName()
    {
        return "qiniu-ecloud";
    }

    @Override
    public StorageAPI getStorageAPI()
    {
        return new QiniuECloudStorage();
    }
}
