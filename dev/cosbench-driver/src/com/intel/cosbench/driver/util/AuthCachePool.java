package com.intel.cosbench.driver.util;

import java.util.concurrent.*;
import com.intel.cosbench.api.context.AuthContext;

/**
 * This class encapsulates an auth cache pool to help cache authenticated AuthContext for reuse.
 *  
 * @author ywang19
 * 
 */
public class AuthCachePool extends ConcurrentHashMap<String, AuthContext>{
	private static final long serialVersionUID = -8592973423618299263L;
	private static AuthCachePool INSTANCE = new AuthCachePool();
	
	private AuthCachePool() {
		
	}
	
	public static AuthCachePool getInstance() {
		return INSTANCE;
	}

}
