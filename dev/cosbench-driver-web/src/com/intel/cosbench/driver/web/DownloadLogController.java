package com.intel.cosbench.driver.web;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import com.intel.cosbench.model.MissionInfo;

public class DownloadLogController extends MissionPageController{
	private static final View LOG = new LogView();

    private static class LogView implements View {

        @Override
        public String getContentType() {
            return "text/plain";
        }

        @Override
        public void render(Map<String, ?> model, HttpServletRequest req,
                HttpServletResponse res) throws Exception {
            String log = (String) model.get("log");
            res.setHeader("Content-Length", String.valueOf(log.length()));
            res.setHeader("Content-Disposition",
                    "attachment; filename=\"mission-log.txt\"");
           res.getOutputStream().write(log.getBytes());
        }

    }

    protected ModelAndView process(MissionInfo info) {
        String log = "";
		try {
			log = info.getLogManager().getLogAsString();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return new ModelAndView(LOG, "log", log);
    }

}
