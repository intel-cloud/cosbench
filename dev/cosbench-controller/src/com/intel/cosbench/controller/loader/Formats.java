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

package com.intel.cosbench.controller.loader;

import java.text.*;

/**
 * This class defines necessary data/number formats.
 * 
 * @author ywang19, qzheng7
 *
 */
class Formats {

    public static final DateFormat TIME = new SimpleDateFormat("HH:mm:ss");

    public static final DateFormat DATETIME = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");

    public static final NumberFormat COUNT = new DecimalFormat(",###");

    public static final NumberFormat NUM = new DecimalFormat("0.##");

    public static final NumberFormat RATIO = new DecimalFormat("0.##%");
    
    public static double getDoubleValue(String column) {
		return column.equalsIgnoreCase("N/A") ? 0D : Double.valueOf(column);
	}
	
	public static int getIntValue(String column) {
		return column.equalsIgnoreCase("N/A") ? 0 : Integer.valueOf(column);
	}
	
	public static long getLongValue(String column) {
		return column.equalsIgnoreCase("N/A") ? 0L : Long.valueOf(column);
	}

}
