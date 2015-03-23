package com.intel.cosbench.controller.web;



import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.springframework.web.servlet.ModelAndView;

import com.intel.cosbench.model.WorkloadInfo;
import com.intel.cosbench.service.ControllerService;
import com.intel.cosbench.web.AbstractController;

public class WorkloadMatrixConfigurationController extends AbstractController {    

    protected ControllerService controller;

    public void setController(ControllerService controller) {
        this.controller = controller;
    }
    
    @Override
    protected ModelAndView process(HttpServletRequest req,
            HttpServletResponse res) {
        
        try {
            constructWorkloadConfigsFromPostData(req);             

        } catch (Exception e) {
            return createErrResult(e.getMessage());
        }

        return createSuccResult();
    }

	private void constructWorkloadConfigsFromPostData(HttpServletRequest req) throws Exception {
		
		WorkloadConfigGenerator wlConfGen = new WorkloadConfigGenerator(controller);
		wlConfGen.createWorkloadFiles(req);
		
	}
	
	 private ModelAndView createErrResult(String msg) {
	        ModelAndView result = new ModelAndView("advanced-config");
	        result.addObject("error", "ERROR: " + msg);
	        result.addObject("cInfo", controller.getControllerInfo());
	        return result;
	    }

	    private ModelAndView createSuccResult() {    	
	        WorkloadInfo[] aInfos = controller.getActiveWorkloads();
	        ModelAndView result = new ModelAndView("submit", "aInfos", aInfos);
	        result.addObject("cInfo", controller.getControllerInfo());
	        return result;
	    }
	    

}
