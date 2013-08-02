package com.intel.cosbench.controller.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.*;

import com.intel.cosbench.utils.AuthValidator;

public class CliLoginFilter implements javax.servlet.Filter {
	protected ServletContext servletContext;

	private Map<String, String> userInfo = new HashMap<String, String>();
	public void init(FilterConfig filterConfig) {
		servletContext = filterConfig.getServletContext();
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
		try {
			if (!AuthValidator.NeedLogon(userInfo)) {
				String username = (String) request.getParameter("username").toString();
				String password = (String) request.getParameter("password").toString();
				if (!username.isEmpty() && !password.isEmpty()) {
					if (userInfo.containsKey(username)
							&& userInfo.get(username).equals(password)) {
						chain.doFilter(request, response);
						return;
					} else {
						response.getWriter().write(
								"User info [username:password] incorrect !");
						return;
					}
				} else {
					response.getWriter()
							.write("Please input complete user info [username:password] to get authentication!");
					return; // need username and password
				}
			} else {
				chain.doFilter(request, response);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

}
