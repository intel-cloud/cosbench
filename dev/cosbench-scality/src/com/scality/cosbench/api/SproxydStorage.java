/**

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

package com.scality.cosbench.api;

import static com.scality.cosbench.client.SproxydConstants.*;

import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.SocketTimeoutException;

import org.apache.http.client.HttpClient;

import com.intel.cosbench.api.storage.NoneStorage;
import com.intel.cosbench.api.storage.StorageException;
import com.intel.cosbench.api.storage.StorageInterruptedException;
import com.intel.cosbench.api.storage.StorageTimeoutException;
import com.intel.cosbench.client.http.HttpClientUtil;
import com.intel.cosbench.config.Config;
import com.intel.cosbench.log.Logger;
import com.scality.cosbench.client.SproxydClient;
import com.scality.cosbench.client.SproxydClientException;

public class SproxydStorage extends NoneStorage {

	private SproxydClient client;
	private String basePath;
	private String hosts;
	private int port;
	private int timeout;
	private boolean logging;

	@Override
	public void init(Config config, Logger logger) {
		super.init(config, logger);
		initParms(config);


		HttpClient httpClient = HttpClientUtil.createHttpClient(timeout);
		client = new SproxydClient(httpClient, hosts, port, basePath, logging ? logger : null);
		logger.debug("sproxyd client has been initialized");
	}

	private void initParms(Config config) {
		basePath = config.get(BASE_PATH_KEY, BASE_PATH_DEFAULT);
		hosts = config.get(HOSTS_KEY, HOSTS_DEFAULT);
		port = config.getInt(PORT_KEY, PORT_DEFAULT);
		timeout = config.getInt(CONN_TIMEOUT_KEY, CONN_TIMEOUT_DEFAULT);
		logging = config.getBoolean(LOGGING_KEY, LOGGING_DEFAULT);
		/* register all parameters */
		parms.put(LOGGING_KEY, logging);

		parms.put(CONN_TIMEOUT_KEY, timeout);

		logger.debug("using storage config: {}", parms);
	}

	@Override
	public void dispose() {
		super.dispose();
		client.dispose();
	}

	@Override
	public void abort() {
		super.abort();
		client.abort();
	}

	@Override
	public InputStream getObject(String container, String object, Config config) {
		super.getObject(container, object, config);
		InputStream stream;
		try {
			logger.info("Retrieving " + container + "\\" + object);
			stream = client.getObjectAsStream(container, object);
		} catch (SocketTimeoutException te) {
			throw new StorageTimeoutException(te);
		} catch (InterruptedIOException ie) {
			throw new StorageInterruptedException(ie);
		} catch (SproxydClientException se) {
			String msg = se.getMessage();//se.getHttpStatusLine().toString();
			throw new StorageException(msg, se);
		} catch (Exception e) {
			throw new StorageException(e);
		}
		return stream;
	}

	@Override
	public void createContainer(String container, Config config) {
		super.createContainer(container, config);
		try {
			logger.info("Creating " + container);
			client.createContainer(container);
		} catch (SocketTimeoutException te) {
			throw new StorageTimeoutException(te);
		} catch (InterruptedIOException ie) {
			throw new StorageInterruptedException(ie);
		} catch (SproxydClientException se) {
			String msg = se.getMessage();//se.getHttpStatusLine().toString();
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
			logger.info("Creating " + container + "\\" + object + " with length=" + length + " Bytes");
			client.storeStreamedObject(container, object, data, length);
		} catch (SocketTimeoutException te) {
			throw new StorageTimeoutException(te);
		} catch (InterruptedIOException ie) {
			throw new StorageInterruptedException(ie);
		} catch (SproxydClientException se) {
			String msg = se.getMessage();//se.getHttpStatusLine().toString();
			throw new StorageException(msg, se);
		} catch (Exception e) {
			throw new StorageException(e);
		}
	}

	@Override
	public void deleteContainer(String container, Config config) {
		super.deleteContainer(container, config);
		try {
			logger.info("Deleting " + container);
			client.deleteContainer(container);
		} catch (SocketTimeoutException te) {
			throw new StorageTimeoutException(te);
		} catch (InterruptedIOException ie) {
			throw new StorageInterruptedException(ie);
		} catch (SproxydClientException se) {
			String msg = se.getMessage();//se.getHttpStatusLine().toString();
			throw new StorageException(msg, se);
		} catch (Exception e) {
			throw new StorageException(e);
		}
	}

	@Override
	public void deleteObject(String container, String object, Config config) {
		super.deleteObject(container, object, config);
		try {
			logger.info("Deleting " + container + "\\" + object);
			client.deleteObject(container, object);
		} catch (SocketTimeoutException te) {
			throw new StorageTimeoutException(te);
		} catch (InterruptedIOException ie) {
			throw new StorageInterruptedException(ie);
		} catch (SproxydClientException se) {
			String msg = se.getHttpStatusLine().toString();
			throw new StorageException(msg, se);
		} catch (Exception e) {
			throw new StorageException(e);
		}
	}

}
