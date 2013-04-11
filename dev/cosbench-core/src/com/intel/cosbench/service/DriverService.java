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

package com.intel.cosbench.service;

import com.intel.cosbench.config.XmlConfig;
import com.intel.cosbench.model.*;


/**
 * The interface for driver service.
 * 
 * @author ywang19, qzheng7
 *
 */
public interface DriverService {

    public String submit(XmlConfig config);

    public void login(String id);

    public void launch(String id);

    public void close(String id);

    public void abort(String id);

    public DriverInfo getDriverInfo();

    public MissionInfo getMissionInfo(String id);

    public MissionInfo[] getActiveMissions();

    public MissionInfo[] getHistoryMissions();

}
