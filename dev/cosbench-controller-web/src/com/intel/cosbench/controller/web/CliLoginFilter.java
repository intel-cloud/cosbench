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
