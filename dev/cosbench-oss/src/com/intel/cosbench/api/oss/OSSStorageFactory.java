package com.intel.cosbench.api.oss;

import com.intel.cosbench.api.storage.StorageAPI;
import com.intel.cosbench.api.storage.StorageAPIFactory;

public class OSSStorageFactory implements StorageAPIFactory {

	@Override
	public String getStorageName() {
		return "oss";
	}

	@Override
	public StorageAPI getStorageAPI() {
		return new OSSStorage();
	}

}
