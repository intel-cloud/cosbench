package com.intel.cosbench.client.swauth.utils;

public class SwiftTokenCache {
	private String token;
	private String storageURL;
	private long version;

	
	public SwiftTokenCache()
	{
		token = "dummyToken";
		storageURL = "dummyStorageURL";
		version = 0;
	}
	
	public String getToken() {
		return token;
	}


	public void setToken(String token) {
		this.token = token;
	}


	public String getStorageURL() {
		return storageURL;
	}


	public void setStorageURL(String storageURL) {
		this.storageURL = storageURL;
	}


	public long getVersion() {
		return version;
	}


	public void setVersion(long version) {
		this.version = version;
	}
	
	public void incrementVersion() {
		this.version++;
	}
}
