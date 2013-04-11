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

package com.intel.cosbench.controller.model;

import com.intel.cosbench.utils.MapRegistry;

public class DriverRegistry extends MapRegistry<DriverContext> {

    public void addDriver(DriverContext driver) {
        addItem(driver);
    }

    public DriverContext getDriver(String name) {
        return (DriverContext) getItem(name);
    }

    public DriverContext[] getAllDrivers() {
        return getAllItems().toArray(new DriverContext[getSize()]);
    }

}
