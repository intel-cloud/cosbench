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

import java.util.ArrayList;
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
    
	private ArrayList<Object> constructNormalStage(HttpServletRequest req) {
		if (req.getParameterValues("normal.checked") != null) {

			String workStageName = new String("normal");
			ArrayList<Object> workStageList = new ArrayList<Object>();
			for (int i = 0; i < req.getParameterValues("normal.checked").length; i++) {
				if (i > 0) {
					workStageName = new String("normal" + i);
				}
				Stage stage = new Stage(workStageName);
				Work work = new Work(workStageName, "normal");
				work.setWorkers(getParmInt(req
						.getParameterValues("normal.workers")[i]));
				work.setRampup(getParmInt(req
						.getParameterValues("normal.rampup")[i]));
				work.setRuntime(getParmInt(req
						.getParameterValues("normal.runtime")[i]));

				// read operation
				String rconfig = "";
				Operation rOp = new Operation("read");

				int rRatio = getParmInt(
						req.getParameterValues("read.ratio")[i], 0);
				rOp.setRatio(rRatio);

				String rcselector = req.getParameterValues("read.containers")[i];
				String rcmin = req.getParameterValues("read.containers.min")[i];
				String rcmax = req.getParameterValues("read.containers.max")[i];
				rconfig += "containers=" + rcselector + "(" + rcmin + ","
						+ rcmax + ");";

				// "objects" section in config
				String roselector = req.getParameterValues("read.objects")[i];
				String romin = req.getParameterValues("read.objects.min")[i];
				String romax = req.getParameterValues("read.objects.max")[i];
				rconfig += "objects=" + roselector + "(" + romin + "," + romax
						+ ");";
				rOp.setConfig(rconfig);

				work.addOperation(rOp);

				// write operation
				String wconfig = "";
				Operation wOp = new Operation("write");

				int wRatio = getParmInt(
						req.getParameterValues("write.ratio")[i], 0);
				wOp.setRatio(wRatio);

				String wcselector = req.getParameterValues("write.containers")[i];
				String wcmin = req.getParameterValues("write.containers.min")[i];
				String wcmax = req.getParameterValues("write.containers.max")[i];
				wconfig += "containers=" + wcselector + "(" + wcmin + ","
						+ wcmax + ");";

				// "objects" section in config
				String woselector = req.getParameterValues("write.objects")[i];
				String womin = req.getParameterValues("write.objects.min")[i];
				String womax = req.getParameterValues("write.objects.max")[i];
				wconfig += "objects=" + woselector + "(" + womin + "," + womax
						+ ");";

				// "sizes" section in config
				String wsselector = req.getParameterValues("write.sizes")[i];
				String wsmin = req.getParameterValues("write.sizes.min")[i];
				String wsmax = req.getParameterValues("write.sizes.max")[i];
				String wsunit = req.getParameterValues("write.sizes.unit")[i];

				String wsexp = "";
				if ("u".equals(wsselector) || "r".equals(wsselector))
					wsexp = wsselector + "(" + wsmin + "," + wsmax + ")"
							+ wsunit;
				if ("c".equals(wsselector))
					wsexp = wsselector + "(" + wsmin + ")" + wsunit;

				wconfig += "sizes=" + wsexp;

				wOp.setConfig(wconfig);

				work.addOperation(wOp);

				// filewrite operation
				String fwconfig = "";
				Operation fwOp = new Operation("filewrite");

				int fwRatio = getParmInt(
						req.getParameterValues("filewrite.ratio")[i], 0);
				fwOp.setRatio(fwRatio);

				// "containers" section in config
				String fwcselector = req
						.getParameterValues("filewrite.containers")[i];
				String fwcmin = req
						.getParameterValues("filewrite.containers.min")[i];
				String fwcmax = req
						.getParameterValues("filewrite.containers.max")[i];
				fwconfig += "containers=" + fwcselector + "(" + fwcmin + ","
						+ fwcmax + ");";

				// "objects" section in config
				String fwoselector = req
						.getParameterValues("filewrite.fileselection")[i];
				fwconfig += "fileselection=" + fwoselector + ";";

				// "files" section in config
				String fwfselector = req.getParameterValues("filewrite.files")[i];
				fwconfig += "files=" + fwfselector;

				fwOp.setConfig(fwconfig);

				work.addOperation(fwOp);

				// delete operation
				String dconfig = "";
				Operation dOp = new Operation("delete");

				int dRatio = getParmInt(
						req.getParameterValues("delete.ratio")[i], 0);
				dOp.setRatio(dRatio);

				String dcselector = req.getParameterValues("delete.containers")[i];
				String dcmin = req.getParameterValues("delete.containers.min")[i];
				String dcmax = req.getParameterValues("delete.containers.max")[i];
				dconfig += "containers=" + dcselector + "(" + dcmin + ","
						+ dcmax + ");";

				// "objects" section in config
				String doselector = req.getParameterValues("delete.objects")[i];
				String domin = req.getParameterValues("delete.objects.min")[i];
				String domax = req.getParameterValues("delete.objects.max")[i];
				dconfig += "objects=" + doselector + "(" + domin + "," + domax
						+ ");";
				dOp.setConfig(dconfig);

				work.addOperation(dOp);

				stage.addWork(work);

				workStageList.add(stage);
			}
			return workStageList;
		}
		
		return null;
	}
    
	private int getParmInt(String string, int defVal) {
		if (string != null)
			return Integer.parseInt(string);
		else
			return defVal;
	}

	private int getParmInt(String string) {
		return Integer.parseInt(string);
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
    	
		ArrayList<Object> normalStageList = constructNormalStage(req);
		if (normalStageList != null) {
			for (int i = 0; i < normalStageList.size(); i++) {
				workflow.addStage((Stage) normalStageList.get(i));
			}
		}	
    	
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
