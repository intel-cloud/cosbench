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

public class AmpliNamespace {
    public static final String NAME = "X-Ampli-Name";
    public static final String POLICY_ID = "X-Ampli-Policy-Id";
    public static final String MASTER_NODE_ID = "X-Ampli-Master-Node-Id";

    public String name;
    public String policy_Id;
    public long master_Node_Id;

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Name: ");
        sb.append(name);
        sb.append("\tPolicyId: ");
        sb.append(policy_Id);
        sb.append("\tMasterNodeId: ");
        sb.append(master_Node_Id);
        sb.append("\n");

        return sb.toString();
    }
}
