package com.intel.cosbench.config.common;

import com.intel.cosbench.config.ConfigConstants;

public class ConfigUtils {

	public static String inherit(String child_config, String parent_config) {
		if(parent_config.equals("")) {
			return child_config;
		}
		if(child_config.equals("")) {
			return parent_config;
		}
		child_config = parent_config + ConfigConstants.DELIMITER + child_config;
		return child_config;
	}
}
