package com.joyent.manta.cosbench;

import com.intel.cosbench.api.storage.StorageAPI;
import com.intel.cosbench.api.storage.StorageAPIFactory;

/**
 * {@link StorageAPIFactory} implementation that defines the properties
 * of the Manta adaptor.
 *
 * @author <a href="https://github.com/dekobon">Elijah Zupancic</a>
 */
public class MantaStorageFactory implements StorageAPIFactory {

    @Override
    public String getStorageName() {
        return "manta";
    }

    @Override
    public StorageAPI getStorageAPI() {
        return new MantaStorage();
    }

}

