package com.intel.cosbench.driver.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.intel.cosbench.service.DriverService;
import com.intel.cosbench.web.AbstractController;

public class TriggerController extends AbstractController {
	protected DriverService driver;

    public void setDriver(DriverService driver) {
        this.driver = driver;
    }
    @Override
    protected ModelAndView process(HttpServletRequest req,
            HttpServletResponse res) throws Exception {
        return new ModelAndView();
    }

}
