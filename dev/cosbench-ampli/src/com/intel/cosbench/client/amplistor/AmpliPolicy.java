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

public class AmpliPolicy {
    public static final String REPAIR_SPREAD = "RepairSpread";
    public static final String DYNAMIC_SAFETY = "DynamicSafety";

    public static final String ID = "X-Ampli-Id";
    public static final String SPREAD_WIDTH = "X-Ampli-Spread-Width";
    public static final String SAFETY = "X-Ampli-Safety";
    public static final String HIERARYCHY_RULES = "X-Ampli-Hierarchy-Rules";
    public static final String MAX_SUPERBLOCK_SIZE = "X-Ampli-Max-Superblock-Size";
    public static final String SAFETY_STRATEGY = "X-Ampli-Safety-Strategy";
    public static final String N_MESSAGES = "X-Ampli-N-Messages";

    public String id;
    public long spread_Width = 16;
    public long safety = 4;
    // public String hierarchy_Rules = "[ true,true,true ]"; // list
    // public ArrayList<Object> hierarchy_Rules;
    public Object[] hierarchy_Rules;

    public long max_Superblock_Size = 32 * 1024 * 1024;
    // public String safety_Strategy = "[ \"" + REPAIR_SPREAD + "\",\"" +
    // DYNAMIC_SAFETY + "\" ]"; // list
    // public ArrayList<Object> safety_Strategy;
    public Object[] safety_Strategy;
    public long n_Messages;

    public AmpliPolicy() {
        spread_Width = 16;
        safety = 4;

        hierarchy_Rules = new Object[2];
        hierarchy_Rules[0] = true;
        hierarchy_Rules[1] = true;
        // hierarchy_Rules = new ArrayList<Object>();
        // hierarchy_Rules.add(true);
        // hierarchy_Rules.add(true);
        // hierarchy_Rules.add(true); // test framework uses 2 level hierarchy
        // rules.

        max_Superblock_Size = 32 * 1024 * 1024;

        safety_Strategy = new Object[2];
        safety_Strategy[0] = REPAIR_SPREAD;
        safety_Strategy[1] = DYNAMIC_SAFETY;

        // safety_Strategy = new ArrayList<Object>();
        // safety_Strategy.add(REPAIR_SPREAD);
        // safety_Strategy.add(DYNAMIC_SAFETY);

        n_Messages = 4096;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Id: ");
        sb.append(id);
        sb.append("\tSpreadWidth: ");
        sb.append(spread_Width);
        sb.append("\tSafety: ");
        sb.append(safety);
        sb.append("\tHierarchyRules: ");
        sb.append(hierarchy_Rules);
        sb.append("\tMaxSuperblockSize: ");
        sb.append(max_Superblock_Size);
        sb.append("\tSafetyStrategy: ");
        sb.append(safety_Strategy);
        sb.append("\tNMessages: ");
        sb.append(n_Messages);
        sb.append("\n");

        return sb.toString();
    }

}
