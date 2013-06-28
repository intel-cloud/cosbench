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

package com.intel.cosbench.api.swift;

import static com.intel.cosbench.client.swift.SwiftConstants.*;

import java.io.*;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.Map;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.nio.pool.BasicNIOConnPool;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.nio.protocol.HttpAsyncRequestExecutor;
import org.apache.http.nio.protocol.HttpAsyncRequester;
import org.apache.http.concurrent.FutureCallback;
import static org.apache.http.HttpStatus.*;

import com.intel.cosbench.api.context.AuthContext;
import com.intel.cosbench.api.storage.*;
import com.intel.cosbench.client.http.HttpClientUtil;
import com.intel.cosbench.client.swift.*;
import com.intel.cosbench.config.Config;
import com.intel.cosbench.log.Logger;
import com.intel.cosbench.driver.util.StatsCallback;
import com.intel.cosbench.bench.Sample;

/**
 * This class encapsulates a Swift implementation for Storage API.
 * 
 * @author ywang19, qzheng7
 * 
 */
class SwiftStorage extends NoneStorage implements FutureCallback<HttpResponse> {

	private BasicNIOConnPool connPool;
	private HttpAsyncRequester requester;
	private HttpHost target;
	private StatsCallback statsCallback;

	/* configurations */
	private int timeout; // connection and socket timeout

	public SwiftStorage(BasicNIOConnPool connPool,
			HttpAsyncRequester requester, HttpHost target,
			StatsCallback statsCallback) {
		this.connPool = connPool;
		this.requester = requester;
		this.target = target;
		this.statsCallback = statsCallback;
	}

	// @Override
	// public void init(Config config, Logger logger) {
	// super.init(config, logger);
	//
	// timeout = config.getInt(CONN_TIMEOUT_KEY, CONN_TIMEOUT_DEFAULT);
	//
	// parms.put(CONN_TIMEOUT_KEY, timeout);
	//
	// logger.debug("using storage config: {}", parms);
	// logger.debug("swift client has been initialized");
	// }

	// @Override
	// public void setAuthContext(AuthContext info) {
	// super.setAuthContext(info);
	// String token = info.getStr(AUTH_TOKEN_KEY);
	// String url = info.getStr(STORAGE_URL_KEY);
	// try {
	// client.init(token, url);
	// } catch (Exception e) {
	// throw new StorageException(e);
	// }
	// logger.debug("using auth token: {}, storage url: {}", token, url);
	// }

	@Override
	public void dispose() {
		super.dispose();
		// client.dispose();
	}

	@Override
	public void abort() {
		super.abort();
		// client.abort();
	}

	@Override
	public InputStream getObject(String container, String object, Config config) {
		super.getObject(container, object, config);
		InputStream stream;
		try {
			stream = getObjectAsStream(container, object);
		} catch (SocketTimeoutException ste) {
			throw new StorageTimeoutException(ste);
		} catch (ConnectTimeoutException cte) {
			throw new StorageTimeoutException(cte);
		} catch (InterruptedIOException ie) {
			throw new StorageInterruptedException(ie);
		} catch (SwiftException se) {
			String msg = se.getHttpStatusLine().toString();
			throw new StorageException(msg, se);
		} catch (Exception e) {
			throw new StorageException(e);
		}
		return stream;
	}

	public Sample getObjectAsStream(String container, String object)
			throws IOException, SwiftException {
		String path = "/" + container + "/" + object;
		BasicHttpRequest request = HttpClientUtil.getRequest("GET", path);
		final long start = System.currentTimeMillis();
		HttpClientUtil.makeRequest(requester, request, connPool, target, path,
				new FutureCallback<HttpResponse>() {
					private boolean status = false;
					public void completed(HttpResponse response) {
						// check return code
						if (response.getStatusLine().getStatusCode() == SC_OK) {
							status = true;
						}
						long end = System.currentTimeMillis();
						// stats counting
						statsCallback.onStats(end - start, status);
					}

					public void failed(Exception ex) {
					}

					public void cancelled() {
					}
				});
		return null;
	}

	@Override
	public void createContainer(String container, Config config) {
		super.createContainer(container, config);
		try {
			if (!client.containerExists(container))
				client.createContainer(container);
		} catch (SocketTimeoutException ste) {
			throw new StorageTimeoutException(ste);
		} catch (ConnectTimeoutException cte) {
			throw new StorageTimeoutException(cte);
		} catch (InterruptedIOException ie) {
			throw new StorageInterruptedException(ie);
		} catch (SwiftException se) {
			String msg = se.getHttpStatusLine().toString();
			throw new StorageException(msg, se);
		} catch (Exception e) {
			throw new StorageException(e);
		}
	}

	@Deprecated
	public void createObject(String container, String object, byte[] data,
			Config config) {
		super.createObject(container, object, data, config);
		try {
			client.storeObject(container, object, data);
		} catch (SocketTimeoutException ste) {
			throw new StorageTimeoutException(ste);
		} catch (ConnectTimeoutException cte) {
			throw new StorageTimeoutException(cte);
		} catch (InterruptedIOException ie) {
			throw new StorageInterruptedException(ie);
		} catch (SwiftException se) {
			String msg = se.getHttpStatusLine().toString();
			throw new StorageException(msg, se);
		} catch (Exception e) {
			throw new StorageException(e);
		}
	}

	@Override
	public void createObject(String container, String object, InputStream data,
			long length, Config config) {
		super.createObject(container, object, data, length, config);
		try {
			client.storeStreamedObject(container, object, data, length);
		} catch (SocketTimeoutException ste) {
			throw new StorageTimeoutException(ste);
		} catch (ConnectTimeoutException cte) {
			throw new StorageTimeoutException(cte);
		} catch (InterruptedIOException ie) {
			throw new StorageInterruptedException(ie);
		} catch (SwiftException se) {
			String msg = se.getHttpStatusLine().toString();
			throw new StorageException(msg, se);
		} catch (Exception e) {
			throw new StorageException(e);
		}
	}

	@Override
	public void deleteContainer(String container, Config config) {
		super.deleteContainer(container, config);
		try {
			if (client.containerExists(container))
				client.deleteContainer(container);
		} catch (SocketTimeoutException ste) {
			throw new StorageTimeoutException(ste);
		} catch (ConnectTimeoutException cte) {
			throw new StorageTimeoutException(cte);
		} catch (InterruptedIOException ie) {
			throw new StorageInterruptedException(ie);
		} catch (SwiftException se) {
			String msg = se.getHttpStatusLine().toString();
			throw new StorageException(msg, se);
		} catch (Exception e) {
			throw new StorageException(e);
		}
	}

	@Override
	public void deleteObject(String container, String object, Config config) {
		super.deleteObject(container, object, config);
		try {
			client.deleteObject(container, object);
		} catch (SocketTimeoutException ste) {
			throw new StorageTimeoutException(ste);
		} catch (ConnectTimeoutException cte) {
			throw new StorageTimeoutException(cte);
		} catch (InterruptedIOException ie) {
			throw new StorageInterruptedException(ie);
		} catch (SwiftException se) {
			String msg = se.getHttpStatusLine().toString();
			throw new StorageException(msg, se);
		} catch (Exception e) {
			throw new StorageException(e);
		}
	}

	@Override
	protected void createMetadata(String container, String object,
			Map<String, String> map, Config config) {
		super.createMetadata(container, object, map, config);
		try {
			client.storeObjectMetadata(container, object, map);
		} catch (SocketTimeoutException ste) {
			throw new StorageTimeoutException(ste);
		} catch (ConnectTimeoutException cte) {
			throw new StorageTimeoutException(cte);
		} catch (InterruptedIOException ie) {
			throw new StorageInterruptedException(ie);
		} catch (SwiftException se) {
			String msg = se.getHttpStatusLine().toString();
			throw new StorageException(msg, se);
		} catch (Exception e) {
			throw new StorageException(e);
		}
	}

	@Override
	protected Map<String, String> getMetadata(String container, String object,
			Config config) {
		super.getMetadata(container, object, config);
		try {
			return client.getObjectMetadata(container, object);
		} catch (SocketTimeoutException ste) {
			throw new StorageTimeoutException(ste);
		} catch (ConnectTimeoutException cte) {
			throw new StorageTimeoutException(cte);
		} catch (InterruptedIOException ie) {
			throw new StorageInterruptedException(ie);
		} catch (SwiftException se) {
			String msg = se.getHttpStatusLine().toString();
			throw new StorageException(msg, se);
		} catch (Exception e) {
			throw new StorageException(e);
		}
	}

	@Override
	public void completed(HttpResponse response) {
		// TODO Auto-generated method stub
		boolean status = false;
		if (response.getStatusLine().getStatusCode() == SC_OK) {
			status = true;
		}
		long end = System.currentTimeMillis();
		Date now = new Date(end);
		Sample sample = new Sample(now, status, end - start);

	}

	@Override
	public void failed(Exception ex) {
		// TODO Auto-generated method stub

	}

	@Override
	public void cancelled() {
		// TODO Auto-generated method stub

	}

}
