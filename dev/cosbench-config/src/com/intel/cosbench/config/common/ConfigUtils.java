/**

Copyright 2013 Intel Corporation, All Rights Reserved.
Copyright 2019 OpenIO Corporation, All Rights Reserved.

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
package com.intel.cosbench.config.common;

import com.intel.cosbench.config.ConfigConstants;

public class ConfigUtils {

    public static String inherit(String child_config, String parent_config) {
        if(parent_config.equals("")) {
            return child_config;
        }
        if(child_config.equals("")) {
            return parent_config;
        }
        child_config = parent_config + ConfigConstants.DELIMITER + child_config;
        return child_config;
    }
}
