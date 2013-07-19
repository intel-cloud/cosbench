/** 
 
Copyright 2013 Intel Corporation, All Rights Reserved.

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

package com.intel.cosbench.api.amplistor;

import static com.intel.cosbench.client.amplistor.AmpliConstants.*;

import java.io.*;
import java.net.SocketTimeoutException;
import java.util.Map;

import org.apache.http.client.HttpClient;

import com.intel.cosbench.api.context.AuthContext;
import com.intel.cosbench.api.storage.*;
import com.intel.cosbench.client.amplistor.*;
import com.intel.cosbench.client.http.HttpClientUtil;
import com.intel.cosbench.config.Config;
import com.intel.cosbench.log.Logger;

/**
 * The AmpliStor implementation for Storage API.
 * 
 * @author ywang19, qzheng7
 * 
 */
public class AmpliStorage extends NoneStorage {

    private AmpliClient client;

    /* configurations */
    private String host;
    private int port;
    private String policy_id;
    private String ns_root;
    private int timeout;

    public AmpliStorage() {
        /* empty */
    }

    @Override
    public void init(Config config, Logger logger) {
        super.init(config, logger);

        host = config.get(HOST_KEY, HOST_DEFAULT);
        port = config.getInt(PORT_KEY, PORT_DEFAULT);
        policy_id = config.get(POLICY_KEY, POLICY_DEFAULT);
        ns_root = config.get(NSROOT_KEY, NSROOT_DEFAULT);
        timeout = config.getInt(TIMEOUT_KEY, TIMEOUT_DEFAULT);

        parms.put(HOST_KEY, host);
        parms.put(PORT_KEY, port);
        parms.put(POLICY_KEY, policy_id);
        parms.put(NSROOT_KEY, ns_root);
        parms.put(TIMEOUT_KEY, timeout);

        logger.debug("using storage config: {}", parms);
        
        HttpClient httpClient = HttpClientUtil.createHttpClient(timeout);
        client = new AmpliClient(httpClient, host, port, ns_root);
        logger.debug("ampli client has been initialized");
    }

    @Override
    public void setAuthContext(AuthContext info) {
        super.setAuthContext(info);
    }

    @Override
    public void dispose() {
        super.dispose();
        client.dispose();
    }

    @Override
    public void abort() {
    	super.abort();
    	client.abort();
    }
    
    @Override
    public InputStream getObject(String container, String object, Config config) {
        super.getObject(container, object, config);
        InputStream stream;
        try {
            stream = client.getObjectAsStream(container, object);
        } catch (SocketTimeoutException te) {
            throw new StorageTimeoutException(te);
        } catch (InterruptedIOException ie) {
            throw new StorageInterruptedException(ie);
        } catch (AmpliException ae) {
            throw new StorageException(ae.getMessage(), ae);
        } catch (Exception e) {
            throw new StorageException(e);
        }
        return stream;
    }

    @Override
    public void createContainer(String container, Config config) {
        super.createContainer(container, config);
        try {
            client.createNamespace(container, policy_id);
        } catch (SocketTimeoutException te) {
            throw new StorageTimeoutException(te);
        } catch (InterruptedIOException ie) {
            throw new StorageInterruptedException(ie);
        } catch (AmpliException ae) {
            throw new StorageException(ae.getMessage(), ae);
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Deprecated
    public void createObject(String container, String object, byte[] data,
            Config config) {
        super.createObject(container, object, data, config);
        try {
            client.StoreObject(data, container, object);
        } catch (SocketTimeoutException te) {
            throw new StorageTimeoutException(te);
        } catch (InterruptedIOException ie) {
            throw new StorageInterruptedException(ie);
        } catch (AmpliException ae) {
            throw new StorageException(ae.getMessage(), ae);
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public void createObject(String container, String object, InputStream data,
            long length, Config config) {
        super.createObject(container, object, data, length, config);
        try {
            client.StoreStreamedObject(data, length, container, object);
        } catch (SocketTimeoutException te) {
            throw new StorageTimeoutException(te);
        } catch (InterruptedIOException ie) {
            throw new StorageInterruptedException(ie);
        } catch (AmpliException ae) {
            throw new StorageException(ae.getMessage(), ae);
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public void deleteContainer(String container, Config config) {
        super.deleteContainer(container, config);
        try {
            client.deleteNamespace(container);
        } catch (SocketTimeoutException te) {
            throw new StorageTimeoutException(te);
        } catch (InterruptedIOException ie) {
            throw new StorageInterruptedException(ie);
        } catch (AmpliException ae) {
            throw new StorageException(ae.getMessage(), ae);
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public void deleteObject(String container, String object, Config config) {
        super.deleteObject(container, object, config);
        try {
            client.deleteObject(container, object);
        } catch (SocketTimeoutException te) {
            throw new StorageTimeoutException(te);
        } catch (InterruptedIOException ie) {
            throw new StorageInterruptedException(ie);
        } catch (AmpliException ae) {
            throw new StorageException(ae.getMessage(), ae);
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    protected void createMetadata(String container, String object,
            Map<String, String> map, Config config) {
        super.createMetadata(container, object, map, config);
        try {
            client.storeObjectMetadata(container, object, map);
        } catch (SocketTimeoutException te) {
            throw new StorageTimeoutException(te);
        } catch (InterruptedIOException ie) {
            throw new StorageInterruptedException(ie);
        } catch (AmpliException ae) {
            throw new StorageException(ae.getMessage(), ae);
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    protected Map<String, String> getMetadata(String container, String object,
            Config config) {
        super.getMetadata(container, object, config);
        try {
            return client.getObjectMetadata(container, object);
        } catch (SocketTimeoutException te) {
            throw new StorageTimeoutException(te);
        } catch (InterruptedIOException ie) {
            throw new StorageInterruptedException(ie);
        } catch (AmpliException ae) {
            throw new StorageException(ae.getMessage(), ae);
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

}
