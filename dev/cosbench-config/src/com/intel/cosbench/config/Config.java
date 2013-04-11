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

package com.intel.cosbench.config;

/**
 * The interface of Configuration class.
 * 
 * @author ywang19, qzheng7
 *
 */
public interface Config {

    public String get(String key);

    public String get(String key, String value);

    public int getInt(String key);

    public int getInt(String key, int value);

    public long getLong(String key);

    public long getLong(String key, long value);

    public double getDouble(String key);

    public double getDouble(String key, double value);

    public boolean getBoolean(String key);

    public boolean getBoolean(String key, boolean value);

}
