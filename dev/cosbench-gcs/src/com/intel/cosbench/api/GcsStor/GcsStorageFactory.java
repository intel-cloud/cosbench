package com.intel.cosbench.api.GcsStor;

import com.intel.cosbench.api.storage.*;

public class GcsStorageFactory implements StorageAPIFactory {

	@Override
	public String getStorageName() {
		return "gcs";
	}

	@Override
	public StorageAPI getStorageAPI() {
		return new GcsStorage();
	}

}
