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

package com.intel.cosbench.config.castor;

import java.io.*;

import com.intel.cosbench.config.*;

/**
 * The castor resolver for mission object, it resolves mission object from xml configuration.
 * 
 * @author ywang19, qzheng7
 *
 */
class CastorMissionResolver extends CastorConfigResolver implements
        MissionResolver {

    @Override
    protected Class<?> getExpectedClass() {
        return Mission.class;
    }

    @Override
    public Mission toMission(XmlConfig config) {
        Mission mission = parseMission(config);
        mission.validate();
        return mission;
    }

    public Mission parseMission(XmlConfig config) {
        String path = config.getPath();
        if (!config.hasContent())
            return (Mission) toDomainObject(path);
        Reader reader = new InputStreamReader(config.getContent());
        return (Mission) toDomainObject(reader, path);
    }

}
