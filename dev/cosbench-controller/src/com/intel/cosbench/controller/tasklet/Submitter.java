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

package com.intel.cosbench.controller.tasklet;

import static com.intel.cosbench.model.TaskState.SUBMITTED;

import com.intel.cosbench.config.*;
import com.intel.cosbench.config.castor.CastorConfigTools;
import com.intel.cosbench.controller.model.*;
import com.intel.cosbench.protocol.SubmitResponse;

/**
 * The class encapsulates how to handle boot request/response, internally, it
 * issues command to ping destination.
 * 
 * @author ywang19, qzheng7
 * 
 */
class Submitter extends AbstractCommandTasklet<SubmitResponse> {

    public Submitter(TaskContext context) {
        super(context, SubmitResponse.class);
    }

    @Override
    public void execute() {
        MissionWriter writer = CastorConfigTools.getMissionWriter();
        Mission mission = createMission();
        issueCommand("submit", writer.toXmlString(mission));
        context.setState(SUBMITTED);
    }

    private Mission createMission() {
        Mission mission = new Mission();
        SchedulePlan plan = context.getSchedule();
        Work work = plan.getWork();
        mission.setName(work.getName());
        mission.setWorkers(plan.getWorkers());
        mission.setOffset(plan.getOffset());
        mission.setInterval(work.getInterval());
        mission.setDivision(work.getDivision());
        mission.setRuntime(work.getRuntime());
        mission.setRampup(work.getRampup());
        mission.setRampdown(work.getRampdown());
        mission.setTotalOps(work.getTotalOps());
        mission.setTotalBytes(work.getTotalBytes());
        mission.setTotalWorkers(work.getWorkers());
        mission.setConfig(work.getConfig());
        mission.setAuth(work.getAuth());
        mission.setStorage(work.getStorage());
        mission.setOperations(work.getOperations());
        LOGGER.debug("controller work config is:" +work.getConfig());
        LOGGER.debug("controller mission config is: "+ mission.getConfig());
        return mission;
    }

    @Override
    protected void handleResponse(SubmitResponse response) {
        String id = response.getId();
        context.setMissionId(id);
    }

}
