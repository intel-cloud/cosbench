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
package com.intel.cosbench.utils;

import static org.apache.commons.lang.SystemUtils.IS_OS_WINDOWS;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class AuthValidator {

    private static final String CFG_FILE_KEY = "cosbench.web.cosbenchUsers";

    private static final String UNIX_DEFAULT_CFG_FILE = "/etc/cosbench/cosbench-users.xml";

    private static final String WIN_DEFAULT_CFG_FILE = "C:\\cosbench-users.xml";

    public static final String USERNAME = "anonymous";
    public static final String PASSWD = "cosbench";

    public AuthValidator() {
        /* empty */
    }

    public static boolean NeedLogon() throws Exception{
        Map<String,String> userInfo = new HashMap<String,String>();
        return NeedLogon(userInfo);
    }

    public static boolean NeedLogon(Map<String,String> userInfo) throws Exception {

        File file = new File(getConfigFile());
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(file);
        doc.getDocumentElement().normalize();

        NodeList userNodeLst = doc.getElementsByTagName("user");

        for (int s = 0; s < userNodeLst.getLength(); s++) {
            Node userNode = userNodeLst.item(s);
            if (userNode.getNodeType() == Node.ELEMENT_NODE) {
                Element Elmnt = (Element) userNode;
                String username = Elmnt.getAttribute("username").toString();
                String password = Elmnt.getAttribute("password").toString();
                if (username.equals(USERNAME) && password.equals(PASSWD))
                    return true;

                if (userInfo != null & !userInfo.containsKey(username))
                    userInfo.put(username, password);
            }
        }
        return false;
    }

    public static String getConfigFile() {
        String configFile;
        if ((configFile = System.getProperty(CFG_FILE_KEY)) != null)
            return configFile;
        if (new File("cosbench-users.xml").exists())
            return "cosbench-users.xml";
        if (new File("conf/cosbench-users.xml").exists())
            return "conf/cosbench-users.xml";
        return IS_OS_WINDOWS ? WIN_DEFAULT_CFG_FILE : UNIX_DEFAULT_CFG_FILE;
    }
}
