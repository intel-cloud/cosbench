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
