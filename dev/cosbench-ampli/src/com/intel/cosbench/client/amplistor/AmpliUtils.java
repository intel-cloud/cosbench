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

package com.intel.cosbench.client.amplistor;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpRequest;

public class AmpliUtils {

    public static AmpliPolicy policyFromResponse(HttpResponse response) {
        AmpliPolicy newPolicy = new AmpliPolicy();
        newPolicy.id = unquotedString(response.getFirstHeader(AmpliPolicy.ID)
                .getValue());
        newPolicy.spread_Width = Long.parseLong(response.getFirstHeader(
                AmpliPolicy.SPREAD_WIDTH).getValue());
        newPolicy.safety = Long.parseLong(response.getFirstHeader(
                AmpliPolicy.SAFETY).getValue());
        newPolicy.hierarchy_Rules = unfold(
                response.getFirstHeader(AmpliPolicy.HIERARYCHY_RULES)
                        .getValue()).toArray();
        newPolicy.max_Superblock_Size = Long.parseLong(response.getFirstHeader(
                AmpliPolicy.MAX_SUPERBLOCK_SIZE).getValue());
        newPolicy.safety_Strategy = unfold(
                response.getFirstHeader(AmpliPolicy.SAFETY_STRATEGY).getValue())
                .toArray();
        newPolicy.n_Messages = Long.parseLong(response.getFirstHeader(
                AmpliPolicy.N_MESSAGES).getValue());

        return newPolicy;
    }

    public static HttpRequest policyToRequest(HttpRequest request,
            AmpliPolicy policy) {
        request.setHeader(AmpliPolicy.SPREAD_WIDTH, new Long(
                policy.spread_Width).toString());
        request.setHeader(AmpliPolicy.SAFETY,
                new Long(policy.safety).toString());
        request.setHeader(AmpliPolicy.HIERARYCHY_RULES,
                fold(policy.hierarchy_Rules));
        request.setHeader(AmpliPolicy.MAX_SUPERBLOCK_SIZE, new Long(
                policy.max_Superblock_Size).toString());
        request.setHeader(AmpliPolicy.SAFETY_STRATEGY,
                fold(policy.safety_Strategy));
        request.setHeader(AmpliPolicy.N_MESSAGES,
                new Long(policy.n_Messages).toString());

        return request;
    }

    public static AmpliNamespace namespaceFromResponse(HttpResponse response) {
        AmpliNamespace newNamespace = new AmpliNamespace();
        newNamespace.name = unquotedString(response.getFirstHeader(
                AmpliNamespace.NAME).getValue());
        newNamespace.policy_Id = unquotedString(response.getFirstHeader(
                AmpliNamespace.POLICY_ID).getValue());
        newNamespace.master_Node_Id = Long.parseLong(response.getFirstHeader(
                AmpliNamespace.MASTER_NODE_ID).getValue());

        return newNamespace;
    }

    public static HttpRequest namespaceToRequest(HttpRequest request,
            AmpliNamespace namespace) {
        request.setHeader(AmpliNamespace.NAME, quotedString(namespace.name));
        request.setHeader(AmpliNamespace.POLICY_ID,
                quotedString(namespace.policy_Id));
        request.setHeader(AmpliNamespace.MASTER_NODE_ID,
                Long.toString(namespace.master_Node_Id));

        return request;
    }

    public static String unquotedString(String str) {
        // if(str.startsWith("\""))
        // {
        // str = str.substring(1);
        // }
        // if(str.endsWith("\""))
        // {
        // str = str.substring(0, str.length()-2);
        // }

        return trim(str, "\"");
    }

    public static String quotedString(String str) {
        return "\"" + str + "\"";
    }

    public static String trim(String str, String delimiter) {
        if (str.startsWith(delimiter)) {
            str = str.substring(delimiter.length());
        }
        if (str.endsWith(delimiter)) {
            str = str.substring(0, str.length() - delimiter.length());
        }

        return str.trim();
    }

    public static String fold(Object[] al) {
        String sl = "[";
        int size = al.length;
        if (size < 1)
            return sl;

        for (int i = 0; i < size - 1; ++i) {
            Object obj = al[i];
            if (obj instanceof String) {
                obj = quotedString((String) obj);
            }
            sl += obj + ",";
        }

        Object obj = al[size - 1];
        if (obj instanceof String) {
            obj = quotedString((String) obj);
        }
        sl += obj + "]";

        return sl;
    }

    public static ArrayList<Object> unfold(String str) {
        ArrayList<Object> al = new ArrayList<Object>();
        str = trim(str, "[");
        str = trim(str, "]");

        String[] s = str.split(",");
        for (int i = 0; i < s.length; i++) {
            String se = s[i].trim();
            // parse
            if (se.equalsIgnoreCase(Boolean.TRUE.toString())
                    || se.equalsIgnoreCase(Boolean.FALSE.toString())) {
                al.add(Boolean.parseBoolean(se));
            } else {
                try {
                    long l = Long.parseLong(se);
                    al.add(l);
                } catch (NumberFormatException e) {
                    al.add(unquotedString(se));
                }
            }

        }

        return al;
    }
}
