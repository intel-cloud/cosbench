/*
 * Copyright 2014-2017 EMC Corporation. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.emc.ecs.cosbench;

import com.intel.cosbench.api.storage.StorageAPI;
import com.intel.cosbench.api.storage.StorageAPIFactory;

/**
 * Factory class for EMC ECS Storage API plugin for
 * COSBench.
 */
public class ECSStorageFactory implements StorageAPIFactory {

    /**
     * Returnes identifying name of this Storage API instance
     */
    public String getStorageName() {
        return "ecs";
    }

    /**
     * Returns a new instance of this Storage API
     */
    public StorageAPI getStorageAPI() {
        return new ECSStorage();
    }
}