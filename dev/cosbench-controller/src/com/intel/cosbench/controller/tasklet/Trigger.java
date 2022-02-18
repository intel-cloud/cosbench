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
package com.intel.cosbench.controller.tasklet;

import org.apache.commons.lang.StringUtils;

import com.intel.cosbench.controller.model.DriverContext;
import com.intel.cosbench.protocol.TriggerResponse;


public class Trigger extends TriggerHttpTasklet {
    public Trigger(DriverContext driver, String trigger, boolean option, String wsId) {
        super(driver, trigger, option, wsId);
    }

    @Override
    public void execute() {
        initHttpClient();
        initObjectMapper();
        String content = getCmdLine();
        if (content == null || content.isEmpty())
            return;
        issueCommand("trigger", content);
        try {
            closeHttpClient();
        } catch (Exception e) {
            LOGGER.error("unexpected exception", e);
        }
    }

    private String getCmdLine() {
        trigger.replace(" ", "");
        int idxLeft = StringUtils.indexOf(trigger, '(');
        int idxRight = StringUtils.indexOf(trigger, ')');
        if (idxLeft < 3 || ( idxRight != trigger.length()-1)
                || !StringUtils.substring(trigger, idxLeft-3, idxLeft).equals(".sh")){
            LOGGER.error("trigger format is illegal, it should be like trigger=\"*.sh(arg1, arg2,...)\"");
            return null;
        }
        scriptName =  StringUtils.left(trigger, idxLeft);
        String argStr = StringUtils.substring(trigger, idxLeft+1, idxRight);
        return isEnable ? ("enableTrigger," + scriptName + "," + argStr)
                : ("killTrigger," + driver.getPidMapValue(scriptName) + "," + scriptName);
    }

    @Override
    protected void handleResponse(TriggerResponse response) {
         driver.putPidMap(scriptName, response.getPID());
        String log = response.getScriptLog();
        if (log == null || log.isEmpty())
            LOGGER.warn("no log for {} on {}", (isEnable ? "enable ":"kill ") + scriptName, driver.getName());
        if (!isEnable) {
            String enableLog = driver.getLogMapValue(wsId);
            driver.putLogMap(wsId, enableLog + log);
            return;
        }
        driver.putLogMap(wsId, scriptName + ";" + log);
    }

}
