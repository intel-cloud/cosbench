package com.intel.cosbench.controller.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.intel.cosbench.service.ControllerService;
import com.intel.cosbench.utils.AuthValidator;
import com.intel.cosbench.web.AbstractController;

public class LogonPageController extends AbstractController {


	@SuppressWarnings("unused")
	private ControllerService controller;

	public void setController(ControllerService controller) {
		this.controller = controller;
	}

	@Override
	protected ModelAndView process(HttpServletRequest req,
			HttpServletResponse res) {
		ModelAndView result = new ModelAndView("logon");
		try {
			if(AuthValidator.NeedLogon()) {
				result.addObject("username", AuthValidator.USERNAME);
				result.addObject("password", AuthValidator.PASSWD);
				result.addObject("hidden","visibility:hidden");
			}
			else{
				result.addObject("username", "");
				result.addObject("password", "");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.addObject("cInfo", controller.getControllerInfo());
		return result;
	}
}
