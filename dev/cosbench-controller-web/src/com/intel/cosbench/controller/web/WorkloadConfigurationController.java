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

package com.intel.cosbench.controller.web;

import java.util.Map;

import javax.servlet.http.*;

import org.apache.commons.io.IOUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import com.intel.cosbench.config.*;
import com.intel.cosbench.config.castor.*;
import com.intel.cosbench.service.ControllerService;
import com.intel.cosbench.web.*;

public class WorkloadConfigurationController extends AbstractController {

    private static final View XML = new XMLView();    

    protected ControllerService controller;

    public void setController(ControllerService controller) {
        this.controller = controller;
    }
    
    private static class XMLView implements View {

        @Override
        public String getContentType() {
            return "application/xml";
        }

        @Override
        public void render(Map<String, ?> model, HttpServletRequest req,
                HttpServletResponse res) throws Exception {
            String xml = (String)model.get("xml");
            res.setHeader("Content-Length", String.valueOf(xml.length()));
            res.setHeader("Content-Disposition",
                    "attachment; filename=\"workload-config.xml\"");

            try {
                IOUtils.write(xml, res.getOutputStream());
            } finally {
            }
        }

    }

    @Override
    protected ModelAndView process(HttpServletRequest req,
            HttpServletResponse res) {
        
    	Workload workload = null;
    	String xml = "";
    	
        try {
            workload = constructWorkloadFromPostData(req);             
        	xml =  CastorConfigTools.getWorkloadWriter().toXmlString(workload);

        } catch (Exception e) {
            return createErrResult(xml, e.getMessage());
        }

        return createSuccResult(xml);
    }

    private Stage constructInitStage(HttpServletRequest req)
    {
    	boolean hasInit = ("on".equalsIgnoreCase(getParm(req, "init.checked")));
    	if(hasInit)
    	{
    		Stage initStage = new Stage("init");
    		Work work = new Work("init", "init");
    		work.setWorkers(getParmInt(req, "init.workers", 1));
    		work.setDivision("container");
    		String config = "";
    		String selector = getParm(req, "init.containers");
    		String min = getParm(req, "init.containers.min");
    		String max = getParm(req, "init.containers.max");
    		config = "containers=" + selector + "(" + min + "," + max + ")";
    		work.setConfig(config);
    		
    		initStage.addWork(work);
    		
    		return initStage;
    	}
    	
    	return null;
    }
    
    private Stage constructPrepareStage(HttpServletRequest req)
    {
    	boolean checked = ("on".equalsIgnoreCase(getParm(req, "prepare.checked")));
    	if(checked)
    	{
    		Stage stage = new Stage("prepare");
    		Work work = new Work("prepare", "prepare");
    		work.setWorkers(getParmInt(req, "prepare.workers", 1));
    		work.setDivision("object");
    		String config = "";
    		
    		// "containers" section in config
    		String cselector = getParm(req, "prepare.containers");
    		String cmin = getParm(req, "prepare.containers.min");
    		String cmax = getParm(req, "prepare.containers.max");
    		config += "containers=" + cselector + "(" + cmin + "," + cmax + ");";
    		
    		// "objects" section in config
    		String oselector = getParm(req, "prepare.objects");
    		String omin = getParm(req, "prepare.objects.min");
    		String omax = getParm(req, "prepare.objects.max");
    		config += "objects=" + oselector + "(" + omin + "," + omax + ");";
    		
    		// "sizes" section in config
    		String sselector = getParm(req, "prepare.sizes");
    		String smin = getParm(req, "prepare.sizes.min");
    		String smax = getParm(req, "prepare.sizes.max");
    		String sunit = getParm(req, "prepare.sizes.unit");
    		
    		String sexp = "";
    		if("u".equals(sselector))
    			sexp = sselector + "(" + smin + "," + smax + ")" + sunit ;
    		if("c".equals(sselector))
    			sexp = sselector + "(" + smin + ")" + sunit;
    		
    		config += "sizes=" + sexp;
    		
    		work.setConfig(config);
    		
    		stage.addWork(work);
    		
    		return stage;
    	}
    	
    	return null;
    }
    
    private Stage constructCleanupStage(HttpServletRequest req)
    {
    	boolean checked = ("on".equalsIgnoreCase(getParm(req, "cleanup.checked")));
    	if(checked)
    	{
    		Stage stage = new Stage("cleanup");
    		Work work = new Work("cleanup", "cleanup");
    		work.setWorkers(getParmInt(req, "cleanup.workers", 1));
    		work.setDivision("object");
    		String config = "";
    		
    		// "containers" section in config
    		String cselector = getParm(req, "cleanup.containers");
    		String cmin = getParm(req, "cleanup.containers.min");
    		String cmax = getParm(req, "cleanup.containers.max");
    		config += "containers=" + cselector + "(" + cmin + "," + cmax + ");";
    		
    		// "objects" section in config
    		String oselector = getParm(req, "cleanup.objects");
    		String omin = getParm(req, "cleanup.objects.min");
    		String omax = getParm(req, "cleanup.objects.max");
    		config += "objects=" + oselector + "(" + omin + "," + omax + ");";
    		
    		work.setConfig(config);
    		
    		stage.addWork(work);
    		
    		return stage;
    	}
    	
    	return null;
    }
    
    private Stage constructDisposeStage(HttpServletRequest req)
    {
    	boolean checked = ("on".equalsIgnoreCase(getParm(req, "dispose.checked")));
    	if(checked)
    	{
    		Stage stage = new Stage("dispose");
    		Work work = new Work("dispose", "dispose");
    		work.setWorkers(getParmInt(req, "dispose.workers", 1));
    		work.setDivision("container");
    		String config = "";
    		
    		// "containers" section in config
    		String cselector = getParm(req, "dispose.containers");
    		String cmin = getParm(req, "dispose.containers.min");
    		String cmax = getParm(req, "dispose.containers.max");
    		config += "containers=" + cselector + "(" + cmin + "," + cmax + ");";
    		
   		
    		work.setConfig(config);
    		
    		stage.addWork(work);
    		
    		return stage;
    	}
    	
    	return null;
    }
    
    private String getParm(HttpServletRequest req, String parm)
    {
    	return req.getParameter(parm);
    }
    
//    private String getParm(HttpServletRequest req, String parm, String defVal)
//    {
//    	String val = getParm(req, parm);
//    	if(val == null || val.isEmpty())
//    		return defVal;
//    	return val;
//    }
    
    private int getParmInt(HttpServletRequest req, String parm)
    {
    	return Integer.parseInt(getParm(req, parm));
    }
    
    private int getParmInt(HttpServletRequest req, String parm, int defVal)
    {
    	String val = getParm(req, parm);
    	if(val == null || val.isEmpty())
    		return defVal;
    	
    	return Integer.parseInt(val);
    }
    
    private Stage constructNormalStage(HttpServletRequest req)
    {
    	boolean checked = ("on".equalsIgnoreCase(getParm(req, "normal.checked")));
    	if(checked)
    	{
    		Stage stage = new Stage("normal");
    		Work work = new Work("normal", "normal");
    		work.setWorkers(getParmInt(req, "normal.workers"));
    		work.setRampup(getParmInt(req, "normal.rampup"));
    		work.setRuntime(getParmInt(req, "normal.runtime"));
    		
    		// read operation
    		String rconfig = "";    		
    		Operation rOp = new Operation("read");
    		
    		int rRatio = getParmInt(req, "read.ratio", 0);
    		rOp.setRatio(rRatio);
    		
    		String rcselector = getParm(req, "read.containers");
    		String rcmin = getParm(req, "read.containers.min");
    		String rcmax = getParm(req, "read.containers.max");
    		rconfig += "containers=" + rcselector + "(" + rcmin + "," + rcmax + ");";
    		
    		// "objects" section in config
    		String roselector = getParm(req, "read.objects");
    		String romin = getParm(req, "read.objects.min");
    		String romax = getParm(req, "read.objects.max");
    		rconfig += "objects=" + roselector + "(" + romin + "," + romax + ");";
    		rOp.setConfig(rconfig);
    		
    		work.addOperation(rOp);
    		
    		
    		// write operation
    		String wconfig = "";
    		Operation wOp = new Operation("write");    		

    		int wRatio = getParmInt(req, "write.ratio", 0);
    		wOp.setRatio(wRatio);
    		
    		String wcselector = getParm(req, "write.containers");
    		String wcmin = getParm(req, "write.containers.min");
    		String wcmax = getParm(req, "write.containers.max");
    		wconfig += "containers=" + wcselector + "(" + wcmin + "," + wcmax + ");";
    		
    		// "objects" section in config
    		String woselector = getParm(req, "write.objects");
    		String womin = getParm(req, "write.objects.min");
    		String womax = getParm(req, "write.objects.max");
    		wconfig += "objects=" + woselector + "(" + womin + "," + womax + ");";
    		
    		// "sizes" section in config
    		String wsselector = getParm(req, "write.sizes");
    		String wsmin = getParm(req, "write.sizes.min");
    		String wsmax = getParm(req, "write.sizes.max");
    		String wsunit = getParm(req, "write.sizes.unit");
    		
    		String wsexp = "";
    		if("u".equals(wsselector) || "r".equals(wsselector))
    			wsexp = wsselector + "(" + wsmin + "," + wsmax + ")" + wsunit ;
    		if("c".equals(wsselector))
    			wsexp = wsselector + "(" + wsmin + ")" + wsunit;
    		
    		wconfig += "sizes=" + wsexp;
    		
    		wOp.setConfig(wconfig);
    		
    		work.addOperation(wOp);
            
            // filewrite operation
            String fwconfig = "";
            Operation fwOp = new Operation("filewrite");
            
            int fwRatio = getParmInt(req, "filewrite.ratio", 0);
            fwOp.setRatio(fwRatio);
            
            // "containers" section in config
            String fwcselector = getParm(req, "filewrite.containers");
            String fwcmin = getParm(req, "filewrite.containers.min");
            String fwcmax = getParm(req, "filewrite.containers.max");
            fwconfig += "containers=" + fwcselector + "(" + fwcmin + "," + fwcmax + ");";
            
            // "objects" section in config
            String fwoselector = getParm(req, "filewrite.fileselection");
            fwconfig += "fileselection=" + fwoselector + ";";
            
            
            // "files" section in config
            String fwfselector = getParm(req, "filewrite.files");
            fwconfig += "files=" + fwfselector;
            
            fwOp.setConfig(fwconfig);
            
            work.addOperation(fwOp);
            
            // delete operation
    		String dconfig = "";
    		Operation dOp = new Operation("delete");    		

    		int dRatio = getParmInt(req, "delete.ratio", 0);
    		dOp.setRatio(dRatio);
    		
    		String dcselector = getParm(req, "delete.containers");
    		String dcmin = getParm(req, "delete.containers.min");
    		String dcmax = getParm(req, "delete.containers.max");
    		dconfig += "containers=" + dcselector + "(" + dcmin + "," + dcmax + ");";
    		
    		// "objects" section in config
    		String doselector = getParm(req, "delete.objects");
    		String domin = getParm(req, "delete.objects.min");
    		String domax = getParm(req, "delete.objects.max");
    		dconfig += "objects=" + doselector + "(" + domin + "," + domax + ");";
    		dOp.setConfig(dconfig);
    		
    		work.addOperation(dOp);
    		
    		stage.addWork(work);
    		
    		return stage;
    	}
    	
    	return null;
    }
    
    private Workload constructWorkloadFromPostData(HttpServletRequest req)
            throws Exception {
    	Workload workload = new Workload();
    	
    	String name = getParm(req, "workload.name");
    	if(name == null || name.isEmpty())
    		name = "workload";
    	String desc = getParm(req, "workload.desc");
    	
    	workload.setName(name);
    	workload.setDescription(desc);
    	
    	workload.setAuth(new Auth(getParm(req, "auth.type"), getParm(req, "auth.config")));
    	workload.setStorage(new Storage(getParm(req, "storage.type"), getParm(req, "storage.config")));

    	Workflow workflow = new Workflow();
    	
    	Stage initStage = constructInitStage(req);    	
    	if(initStage != null) workflow.addStage(initStage);
    	
    	Stage prepareStage = constructPrepareStage(req);    	
    	if(prepareStage != null) workflow.addStage(prepareStage);    	
    	
    	Stage normalStage = constructNormalStage(req);    	
    	if(normalStage != null) workflow.addStage(normalStage);    	
    	
    	Stage cleanupStage = constructCleanupStage(req);    	
    	if(cleanupStage != null) workflow.addStage(cleanupStage);    
    	
    	Stage disposeStage = constructDisposeStage(req);    	
    	if(disposeStage != null) workflow.addStage(disposeStage);    
    	
    	workload.setWorkflow(workflow);
    	
    	workload.validate();
    	
    	return workload;
    }
    
    private ModelAndView createErrResult(String xml, String msg) {
        ModelAndView result = new ModelAndView("config", "xml", xml);
        result.addObject("error", "ERROR: " + msg);
        
        return result;
    }

    private ModelAndView createSuccResult(String xml) {    	
        ModelAndView result = new ModelAndView(XML, "xml", xml);

        return result;
    }
    
}
