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

package com.intel.cosbench.api.storage;

import java.io.InputStream;

import com.intel.cosbench.api.context.*;
import com.intel.cosbench.config.Config;
import com.intel.cosbench.log.Logger;

public interface StorageAPI {

    /**
     * Initializes a <code>Storage-API</code> with parameters contained in the
     * given <code>config</code>, whose content depends on the specific storage
     * type. Normally, it will also initialize one client for storage access.
     * 
     * @param config
     *            - one instance from com.intel.cosbench.config.Config, which
     *            includes parameters for authentication, and it will be passed
     *            from execution engine.
     * @param logger
     *            - one instance from com.intel.cosbench.log.Logger, which
     *            delivers logging capabilities to Storage-API, and it will be passed
     *            from execution engine.
     */
    public void init(Config config, Logger logger);

    /**
     * Releases the resources held by this Storage-API.
     */
    public void dispose();

    /**
     * Retrieves parameters and current settings used by the Storage-API.
     * 
     * @return Context - one Context instance which contains all parameters
     *         configured for the storage.
     */
    public Context getParms();

    /**
     * Aborts the execution of an on-going storage operation (HTTP request) if
     * there is one. 
     * The method expects to provide one approach to abort outstanding operations gracefully 
     * when the worker hits some termination criteria.
     */
    public void abort();

    /**
     * Associates authentication context with this Storage-API for further
     * storage operations.
     * 
     * @param info
     *            - one AuthContext instance, normally, it's the result returned
     *            by the <code>login()</code> from the Auth-API.
     */
    public void setAuthContext(AuthContext info);

    /**
     * @return AuthContext instance associated with this Storage-API
     */
    public AuthContext getAuthContext();

    /**
     * Downloads an object from a container.
     * 
     * @param container
     *            - the name of a container.
     * @param object
     *            - the name of an object to be downloaded.
     * @param config
     *            - the configuration used for this operation.
     */
    public InputStream getObject(String container, String object, Config config);

    /**
     * Gets a list of containers/objects
     * 
     * @param container
     *            - the name of a container.
     * @param object
     *            - the name of an object.
     * @param config
     *            - the configuration used for this operation.
     */
    public InputStream getList(String container, String object, Config config);
    
    /**
     * Creates a new container.
     * 
     * @param container
     *            - the name of a container.
     * @param config
     *            - the configuration used for this operation.
     */
    public void createContainer(String container, Config config);

    /**
     * Uploads an object into a given container.
     * 
     * @param container
     *            - the name of a container.
     * @param object
     *            - the name of an object to be uploaded.
     * @param data
     *            - the inputStream of the object content.
     * @param length
     *            - the length of object content.
     * @param config
     *            - the configuration used for this operation.
     */
    public void createObject(String container, String object, InputStream data,
            long length, Config config);

    /**
     * Removes a given container.
     * 
     * @param container
     *            - the name of a container to be removed.
     * @param config
     *            - the configuration used for this operation.
     */
    public void deleteContainer(String container, Config config);

    /**
     * Deletes a given object.
     * 
     * @param container
     *            - the name of a container.
     * @param object
     *            - the name of an object to be deleted.
     * @param config
     *            - the configuration used for this operation.
     */
    public void deleteObject(String container, String object, Config config);

//    public Map<String, String> getMetadata(String container, String object,
//     Config config);
//    
//    public void createMetadata(String container, String object, Map<String,
//     String> map, Config config);
    
    /**
     * set the current authorization validity 
     * @param auth
     *          - if the current authorization valid or not
     */				
    public void setAuthFlag(Boolean auth);
    /**
     * check if the current authorization valid 
     * 
     */
    public Boolean isAuthValid();

}
