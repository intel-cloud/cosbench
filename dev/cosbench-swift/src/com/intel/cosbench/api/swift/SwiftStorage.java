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
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ConnectTimeoutException;
<<<<<<< HEAD
import org.apache.http.impl.nio.pool.BasicNIOConnPool;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.nio.protocol.HttpAsyncRequestExecutor;
import org.apache.http.nio.protocol.HttpAsyncRequester;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.InputStreamEntity;

=======
import org.apache.http.message.BasicHttpRequest;
>>>>>>> 01b77a30b4640d36ad28230ca216963236066af0
import static org.apache.http.HttpStatus.*;

import com.intel.cosbench.api.context.AuthContext;
import com.intel.cosbench.api.context.IOEngineContext;
import com.intel.cosbench.api.storage.*;
import com.intel.cosbench.client.http.HttpClientUtil;
import com.intel.cosbench.client.swift.*;
import com.intel.cosbench.config.Config;
import com.intel.cosbench.log.Logger;
import com.intel.cosbench.driver.util.HttpResponseCallback;
import com.intel.cosbench.driver.util.StatsCallback;
import com.intel.cosbench.driver.operator.Session;
import com.intel.cosbench.bench.Sample;

/**
 * This class encapsulates a Swift implementation for Storage API.
 * 
 * @author ywang19, qzheng7
 * 
 */
class SwiftStorage extends NoneStorage {

	private BasicNIOConnPool connPool;
	private HttpAsyncRequester requester;
	private HttpHost target;
	private StatsCallback statsCallback;

	/* user context */
	private String authToken;
	private String storageURL;

	/* configurations */
	private int timeout; // connection and socket timeout

	public SwiftStorage() {
		/* empty */
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
	//
	// HttpClient httpClient = HttpClientUtil.createHttpClient(timeout);
	// client = new SwiftClient(httpClient);
	// logger.debug("swift client has been initialized");
	// }
	//
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
	public void init(Config config, Logger logger) {
		super.init(config, logger);

		timeout = config.getInt(CONN_TIMEOUT_KEY, CONN_TIMEOUT_DEFAULT);

		parms.put(CONN_TIMEOUT_KEY, timeout);

		logger.debug("using storage config: {}", parms);
		logger.debug("swift client has been initialized");
	}

	public void setStatsCallback(StatsCallback statsCallback) {
		this.statsCallback = statsCallback;
	}

	public void setIOEngineContext(IOEngineContext context) {
		this.requester = (HttpAsyncRequester) context.get(
				IOEngineContext.REQUESTER_KEY, null);
		this.connPool = (BasicNIOConnPool) context.get(
				IOEngineContext.CONNPOOL_EKY, null);
		this.target = (HttpHost) context.get(IOEngineContext.TARGET_KEY, null);
	}

	@Override
	public void setAuthContext(AuthContext info) {
		super.setAuthContext(info);
		String token = info.getStr(AUTH_TOKEN_KEY);
		String url = info.getStr(STORAGE_URL_KEY);
		try {
			this.authToken = token;
			this.storageURL = url;
		} catch (Exception e) {
			throw new StorageException(e);
		}
		logger.debug("using auth token: {}, storage url: {}", token, url);
	}

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
	public InputStream getObject(String container, String object,
			Config config, Session session) {
		super.getObject(container, object, config, session);
		// InputStream stream;
		try {
			getObjectAsStream(container, object, session);
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
		return null;
	}

	public void getObjectAsStream(String container, String object,
			Session session) throws IOException, Exception {
		String path = "/" + container + "/" + object;
		BasicHttpRequest request = HttpClientUtil.getRequest("GET", path);
		final long start = System.currentTimeMillis();
		HttpClientUtil.makeRequest(requester, request, connPool, target, path,
				new HttpResponseCallback(start, session.getStatsCallback()));
	}

	@Override
	public void createContainer(String container, Config config) {
		super.createContainer(container, config);
		try {
			if (!containerExists(container))
				createContainer(container);
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

	public boolean containerExists(String container) throws IOException,
			HttpException {
		try {
			getContainerInfo(container);
		} catch (SwiftException se) {
			return false;
		}
		return true;
	}

	private String getContainerPath(String container) {
		return storageURL + "/" + HttpClientUtil.encodeURL(container);
	}

	private String getObjectPath(String container, String object) {
		return getContainerPath(container) + "/"
				+ HttpClientUtil.encodeURL(object);
	}

	public SwiftContainer getContainerInfo(String container, Session session)
			throws IOException, SwiftException {
		SwiftResponse response = null;
		try {
			BasicHttpRequest request = HttpClientUtil.getRequest("HEAD",
					container);
			request.setHeader(X_AUTH_TOKEN, authToken);
			HttpClientUtil.makeRequest(requester,request,connPool,target,container,
					new HttpResponseCallback(start, session.getStatsCallback()));

			response = new SwiftResponse(client.execute(method));
			if (response.getStatusCode() == SC_NO_CONTENT) {
				long bytesUsed = response.getContainerBytesUsed();
				int objectCount = response.getContainerObjectCount();
				return new SwiftContainer(container, bytesUsed, objectCount);
			}
			if (response.getStatusCode() == SC_NOT_FOUND)
				throw new SwiftFileNotFoundException("container not found: "
						+ container, response.getResponseHeaders(),
						response.getStatusLine());
			throw new SwiftException("unexpected return from server",
					response.getResponseHeaders(), response.getStatusLine());
		} finally {
			if (response != null)
				response.consumeResposeBody();
		}
	}

	// @Deprecated
	// public void createObject(String container, String object, byte[] data,
	// Config config) {
	// super.createObject(container, object, data, config);
	// try {
	// //storeObject(container, object, data);
	// } catch (SocketTimeoutException ste) {
	// throw new StorageTimeoutException(ste);
	// } catch (ConnectTimeoutException cte) {
	// throw new StorageTimeoutException(cte);
	// } catch (InterruptedIOException ie) {
	// throw new StorageInterruptedException(ie);
	// } catch (SwiftException se) {
	// String msg = se.getHttpStatusLine().toString();
	// throw new StorageException(msg, se);
	// } catch (Exception e) {
	// throw new StorageException(e);
	// }
	// }

	@Override
	public void createObject(String container, String object, InputStream data,
			long length, Config config, Session session) {
		super.createObject(container, object, data, length, config, session);
		try {
			storeStreamedObject(container, object, data, length, session);
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

	public void storeStreamedObject(String container, String object,
			InputStream data, long length, Session session) throws IOException,
			SwiftException, Exception {
		// SwiftResponse response = null;
		// try {
		String path = "/" + container + "/" + object;
		// BasicHttpRequest request = HttpClientUtil.getRequest("PUT", path);
		BasicHttpEntityEnclosingRequest request = new BasicHttpEntityEnclosingRequest(
				"PUT", container);
		request.setHeader(X_AUTH_TOKEN, authToken);
		InputStreamEntity entity = new InputStreamEntity(data, length);
		if (length < 0)
			entity.setChunked(true);
		else
			entity.setChunked(false);
		entity.setContentType("application/octet-stream");
		request.setEntity(entity);
		final long start = System.currentTimeMillis();
		HttpClientUtil.makeRequest(requester, request, connPool, target, path,
				new HttpResponseCallback(start, session.getStatsCallback()));
		//
		// response = new SwiftResponse(client.execute(method));
		// if (response.getStatusCode() == SC_CREATED)
		// return;
		// if (response.getStatusCode() == SC_ACCEPTED)
		// return;
		// if (response.getStatusCode() == SC_NOT_FOUND)
		// throw new SwiftFileNotFoundException("container not found: "
		// + container, response.getResponseHeaders(),
		// response.getStatusLine());
		// throw new SwiftException("unexpected return from server",
		// response.getResponseHeaders(), response.getStatusLine());
		// } finally {
		// if (response != null)
		// response.consumeResposeBody();
		// }
	}

	@Override
	public void deleteContainer(String container, Config config, Session session) {
		super.deleteContainer(container, config, session);
		try {
			if (containerExists(container))
				deleteContainer(container, session);
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

	public void deleteContainer(String container, Session session)
			throws IOException, SwiftException, Exception {
//		SwiftResponse response = null;
		// try {

		BasicHttpRequest request = HttpClientUtil.getRequest("DELETE",
				getContainerPath(container));
		request.setHeader(X_AUTH_TOKEN, authToken);
		final long start = System.currentTimeMillis();
		HttpClientUtil.makeRequest(requester, request, connPool, target,
				getContainerPath(container), new HttpResponseCallback(start,
						session.getStatsCallback()));

		// method = HttpClientUtil.makeHttpDelete(getContainerPath(container));
		// method.setHeader(X_AUTH_TOKEN, authToken);
		// response = new SwiftResponse(client.execute(method));
		// if (response.getStatusCode() == SC_NO_CONTENT)
		// return;
		// if (response.getStatusCode() == SC_NOT_FOUND)
		// throw new SwiftFileNotFoundException("container not found: "
		// + container, response.getResponseHeaders(),
		// response.getStatusLine());
		// if (response.getStatusCode() == SC_CONFLICT)
		// throw new SwiftConflictException(
		// "cannot delete an non-empty container",
		// response.getResponseHeaders(), response.getStatusLine());
		// throw new SwiftException("unexpected return from server",
		// response.getResponseHeaders(), response.getStatusLine());
		// } finally {
		// if (response != null)
		// response.consumeResposeBody();
		// }
	}

	@Override
	public void deleteObject(String container, String object, Config config,
			Session session) {
		super.deleteObject(container, object, config, session);
		try {
			deleteObject(container, object, session);
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

	public void deleteObject(String container, String object, Session session)
			throws IOException, SwiftException, Exception {
		// SwiftResponse response = null;
		// try {
		String path = "/" + container + "/" + object;
		BasicHttpRequest request = HttpClientUtil.getRequest("DELETE",
				getObjectPath(container, object));
		request.setHeader(X_AUTH_TOKEN, authToken);
		final long start = System.currentTimeMillis();
		HttpClientUtil.makeRequest(requester, request, connPool, target, path,
				new HttpResponseCallback(start, session.getStatsCallback()));

		// method = HttpClientUtil.makeHttpDelete(getObjectPath(container,
		// object));
		// method.setHeader(X_AUTH_TOKEN, authToken);
		// response = new SwiftResponse(client.execute(method));
		// if (response.getStatusCode() == SC_NO_CONTENT)
		// return;
		// if (!REPORT_DELETE_ERROR)
		// return;
		// if (response.getStatusCode() == SC_NOT_FOUND)
		// throw new SwiftFileNotFoundException("object not found: "
		// + container + "/" + object,
		// response.getResponseHeaders(), response.getStatusLine());
		// throw new SwiftException("unexpected return from server",
		// response.getResponseHeaders(), response.getStatusLine());
		// } finally {
		// if (response != null)
		// response.consumeResposeBody();
		// }
	}

	@Override
	protected void createMetadata(String container, String object,
			Map<String, String> map, Config config, Session session) {
		super.createMetadata(container, object, map, config, session);
		try {
			storeObjectMetadata(container, object, map, session);
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

	public void storeObjectMetadata(String container, String object,
			Map<String, String> map, Session session) throws IOException,
			SwiftException, Exception {
		SwiftResponse response = null;
		// try {
		BasicHttpEntityEnclosingRequest request = new BasicHttpEntityEnclosingRequest(
				"POST", getObjectPath(container, object));
		request.setHeader(X_AUTH_TOKEN, authToken);

		// method = HttpClientUtil.makeHttpPost(getObjectPath(container,
		// object));
		// method.setHeader(X_AUTH_TOKEN, authToken);
		for (String ele : map.keySet())
			request.addHeader(ele, map.get(ele));

		final long start = System.currentTimeMillis();
		HttpClientUtil.makeRequest(requester, request, connPool, target,
				getObjectPath(container, object), new HttpResponseCallback(
						start, session.getStatsCallback()));

		// response = new SwiftResponse(client.execute(method));
		// if (response.getStatusCode() == SC_ACCEPTED)
		// return;
		// if (response.getStatusCode() == SC_NOT_FOUND)
		// throw new SwiftFileNotFoundException("object not found: "
		// + container + "/" + object,
		// response.getResponseHeaders(), response.getStatusLine());
		// throw new SwiftException("unexpected return from server",
		// response.getResponseHeaders(), response.getStatusLine());
		// } finally {
		// if (response != null)
		// response.consumeResposeBody();
		// }
	}

	@Override
	protected Map<String, String> getMetadata(String container, String object,
			Config config, Session session) {
		super.getMetadata(container, object, config, session);
		try {
			return getObjectMetadata(container, object, session);
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

	public Map<String, String> getObjectMetadata(String container,
			String object, Session session) throws IOException, SwiftException,
			Exception {
		SwiftResponse response = null;
		// try {

		String path = "/" + container + "/" + object;
		BasicHttpRequest request = HttpClientUtil.getRequest("HEAD",
				getObjectPath(container, object));
		request.setHeader(X_AUTH_TOKEN, authToken);
		final long start = System.currentTimeMillis();
		HttpClientUtil.makeRequest(requester, request, connPool, target, path,
				new HttpResponseCallback(start, session.getStatsCallback()));
		return null;

		// method = HttpClientUtil.makeHttpHead(getObjectPath(container,
		// object));
		// method.setHeader(X_AUTH_TOKEN, authToken);
		// response = new SwiftResponse(client.execute(method));
		// if (response.getStatusCode() == SC_OK) {
		// Header[] headers = response.getResponseHeaders();
		// Map<String, String> map = new HashMap<String, String>();
		// for (Header header : headers)
		// map.put(header.getName(), header.getValue());
		// return map;
		// }
		// if (response.getStatusCode() == SC_NOT_FOUND)
		// throw new SwiftFileNotFoundException("object not found: "
		// + container + "/" + object,
		// response.getResponseHeaders(), response.getStatusLine());
		// throw new SwiftException("unexpected result from server",
		// response.getResponseHeaders(), response.getStatusLine());
		// } finally {
		// if (response != null)
		// response.consumeResposeBody();
		// }
	}
}
