package com.intel.cosbench.controller.tasklet;

import org.apache.commons.lang.StringUtils;

import com.intel.cosbench.controller.model.DriverContext;
import com.intel.cosbench.protocol.TriggerResponse;


public class Trigger extends TriggerHttpTasklet {
	public Trigger(DriverContext driver, String trigger, boolean option, String wid) {
		super(driver, trigger, option, wid);
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
	    if (idxLeft < 3 || ( idxRight != trigger.length()-1)){ 
	    	LOGGER.error("can't enable trigger, the format is illegal!");
	    	return null;
	    }
	    scriptName =  StringUtils.left(trigger, idxLeft);
	    String argStr = StringUtils.substring(trigger, idxLeft+1, idxRight);
	    return isEnable ? ("enableTrigger," + scriptName + "," + argStr + "," + wID)
	    		: ("killTrigger," + driver.getPIDMap(scriptName) + "," + scriptName + "," + wID);
	}
    
    @Override
    protected void handleResponse(TriggerResponse response) {
        if (!isEnable) {
        	driver.putPIDMap(scriptName, "0");
			return;
		}
        driver.putPIDMap(scriptName, response.getPID());
    }
    
}
