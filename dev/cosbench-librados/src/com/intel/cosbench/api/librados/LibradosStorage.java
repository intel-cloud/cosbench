/** 
 
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. 
 */

package com.intel.cosbench.api.librados;

import static com.intel.cosbench.client.librados.LibradosConstants.AUTH_PASSWORD_DEFAULT;
import static com.intel.cosbench.client.librados.LibradosConstants.AUTH_PASSWORD_KEY;
import static com.intel.cosbench.client.librados.LibradosConstants.AUTH_USERNAME_DEFAULT;
import static com.intel.cosbench.client.librados.LibradosConstants.AUTH_USERNAME_KEY;
import static com.intel.cosbench.client.librados.LibradosConstants.ENDPOINT_DEFAULT;
import static com.intel.cosbench.client.librados.LibradosConstants.ENDPOINT_KEY;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.ceph.rados.IoCTX;
import com.ceph.rados.Rados;
import com.ceph.rados.RadosException;
import com.intel.cosbench.api.context.AuthContext;
import com.intel.cosbench.api.storage.NoneStorage;
import com.intel.cosbench.api.storage.StorageException;
import com.intel.cosbench.config.Config;
import com.intel.cosbench.log.Logger;

/**
 * LibradosStorage provides methods to access a Storage using librados.
 * It is based on rados-java
 * {@link https://github.com/wido/rados-java}
 * 
 * 
 * @author Niklas Goerke - niklas974@github
 *
 */
public class LibradosStorage extends NoneStorage {

    private String accessKey;
    private String secretKey;
    private String endpoint;

    private static Rados client;

    public void init(Config config, Logger logger) {
        super.init(config, logger);

        this.endpoint = config.get(ENDPOINT_KEY, ENDPOINT_DEFAULT);
        this.accessKey = config.get(AUTH_USERNAME_KEY, AUTH_USERNAME_DEFAULT);
        this.secretKey = config.get(AUTH_PASSWORD_KEY, AUTH_PASSWORD_DEFAULT);

        parms.put(ENDPOINT_KEY, endpoint);
        parms.put(AUTH_USERNAME_KEY, accessKey);
        parms.put(AUTH_PASSWORD_KEY, secretKey);
        logger.debug("using storage config: {}", parms);

        if (client == null) {
            client = new Rados(this.accessKey);
            try {
                client.confSet("key", this.secretKey);
                client.confSet("mon_host", this.endpoint);
                client.connect();
                logger.debug("Librados client has been initialized");
            } catch (RadosException e) {
                throw new StorageException(e);
            }
        }
    }

    public void setAuthContext(AuthContext info) {
        super.setAuthContext(info);
    }

    public void dispose() {
        super.dispose();
//        client = null;
    }

    public InputStream getObject(String container, String object, Config config) {
        super.getObject(container, object, config);
        InputStream stream;
        IoCTX ioctx;
        try {
            ioctx = client.ioCtxCreate(container);
            long length = ioctx.stat(object).getSize();
            if (length > (Math.pow(2, 31) - 1)) {
                throw new StorageException("Object larger than 2GB, handling not implemented");
                // TODO: implement - read in parts and concatinate
            }
            
            byte[] buf = new byte[(int) length];
            ioctx.read(object, (int) length, 0, buf);
            stream = new ByteArrayInputStream(buf);
        } catch (RadosException e) {
            throw new StorageException(e);
        }
        client.ioCtxDestroy(ioctx);
        return stream;
    }

    public void createContainer(String container, Config config) {
        super.createContainer(container, config);
        try {
            boolean exists = false;
            for (String pool : client.poolList()) {
                if (pool.equals(container)) {
                    exists = true;
                }
            }
            if (!exists) {
                client.poolCreate(container);    
            }
        } catch (RadosException e) {
            throw new StorageException(e);
        }
    }

    public void deleteContainer(String container, Config config) {
        super.deleteContainer(container, config);
        try {
            client.poolDelete(container);
        } catch (RadosException e) {
            throw new StorageException(e);
        }
    }

    public void createObject(String container, String object, InputStream data, long length, Config config) {
        super.createObject(container, object, data, length, config);
        byte[] buf = new byte[(int) length];
        try {
            data.read(buf, 0, (int) length);
            IoCTX ioctx = client.ioCtxCreate(container);
            ioctx.write(object, buf);
        } catch (RadosException e) {
            throw new StorageException(e);
        } catch (IOException e) {
            throw new StorageException(e);
        }
    }

    public void deleteObject(String container, String object, Config config) {
        super.deleteObject(container, object, config);
        IoCTX ioctx;
        try {
            ioctx = client.ioCtxCreate(container);
            ioctx.remove(object);
        } catch (RadosException e) {
            throw new StorageException(e);
        }
    }
}
