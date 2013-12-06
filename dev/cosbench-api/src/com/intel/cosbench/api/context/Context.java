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

package com.intel.cosbench.api.context;

import java.util.*;

public class Context {

    private Map<String, Object> params;

    public Context() {
        params = new HashMap<String, Object>();
    }

    public void put(String key, Object val) {
        params.put(key, val);
    }

    public Object get(String key) {
        return get(key, null);
    }

    public Object get(String key, Object defVal) {
        Object val = params.get(key);
        return val == null ? defVal : val;
    }

    public String getStr(String key) {
        return getStr(key, null);
    }

    public String getStr(String key, String defVal) {
        Object val = params.get(key);
        return val == null ? defVal : val.toString();
    }

    public int getInt(String key) {
        return getInt(key, 0);
    }

    public int getInt(String key, int defVal) {
        Object val = params.get(key);
        if (val == null)
            return defVal;
        try {
            return Integer.parseInt(val.toString());
        } catch (Exception e) {
            return defVal;
        }
    }

    public long getLong(String key) {
        return getLong(key, 0L);
    }

    public long getLong(String key, long defVal) {
        Object val = params.get(key);
        if (val == null)
            return defVal;
        try {
            return Long.parseLong(val.toString());
        } catch (Exception e) {
            return defVal;
        }
    }

    public double getDouble(String key) {
        return getDouble(key, 0D);
    }

    public double getDouble(String key, double defVal) {
        Object val = params.get(key);
        if (val == null)
            return defVal;
        try {
            return Double.parseDouble(val.toString());
        } catch (Exception e) {
            return defVal;
        }
    }

    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean defVal) {
        Object val = params.get(key);
        if (val == null)
            return defVal;
        return Boolean.parseBoolean(val.toString());
    }

    @Override
    public String toString() {
        return params.toString();
    }

}
