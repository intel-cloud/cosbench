package com.intel.cosbench.client.cdmi.base;

/**
 * This class provides a method to create corresponding client to access cdmi compatible server in different form (like cdmi content type or non-cdmi content type).
 * 
 * @author ywang19
 *
 */
public class CdmiClientFactory {

	public static BaseCdmiClient getClient(String type) {
		if("cdmi".equalsIgnoreCase(type)) { // cdmi content type
			return new CdmiClient();
		}else if("non-cdmi".equalsIgnoreCase(type)) {
			return new NonCdmiClient();
		}else {
			System.err.println("Type: " + type + " is not supported yet.");
			return null;
		}
	}
}
