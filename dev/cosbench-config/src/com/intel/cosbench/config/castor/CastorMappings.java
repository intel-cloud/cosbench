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

import java.net.URL;


/**
 * the collection of model mapping files
 * 
 * @author ywang19, qzheng7
 *
 */
interface CastorMappings {

    URL AUTH = CastorMappings.class.getResource("auth-mapping.xml");

    URL STORAGE = CastorMappings.class.getResource("storage-mapping.xml");

    URL OPERATION = CastorMappings.class.getResource("operation-mapping.xml");

    URL WORK = CastorMappings.class.getResource("work-mapping.xml");

    URL MISSION = CastorMappings.class.getResource("mission-mapping.xml");

    URL WORKSTAGE = CastorMappings.class.getResource("stage-mapping.xml");

    URL WORKFLOW = CastorMappings.class.getResource("workflow-mapping.xml");

    URL WORKLOAD = CastorMappings.class.getResource("workload-mapping.xml");

}
