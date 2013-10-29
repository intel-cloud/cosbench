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

package com.scality.cosbench.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.util.EntityUtils;

import com.intel.cosbench.client.http.HttpClientUtil;
import com.intel.cosbench.log.Logger;

public class SproxydClient {

	public static final int DEFAULT_SO_TIMEOUT = 2000;
	private static final int DEFAULT_CHECK_PERIOD = 10000;
	private static Logger LOG = null;
	private HttpClient client;
	private SproxydUrlList urls;
	private final Map<Endpoint, SproxydEndpoint> endpoints = new HashMap<Endpoint, SproxydEndpoint>();
	/* current operation */
	private volatile HttpUriRequest request;

	public SproxydClient(HttpClient client, String hosts, int port, String basePath, Logger logger) {
		this.client = client;
		urls = new SproxydUrlList(hosts, port, basePath);
		urls.registerEndpoints(endpoints);
		LOG = logger;
	}

	public void dispose() {
		request = null;
	}

	public void abort() {
		if (request != null)
			request.abort();
		request = null;
	}

	public void createContainer(String container) throws IOException,
	SproxydClientException {
		// sproxyd by path, container will used as path prefix but does not actually exist
	}

	public void deleteContainer(String container) throws IOException,
	SproxydClientException {
		// see createContainer
	}

	public InputStream getObjectAsStream(String container, String object)
			throws IOException, SproxydClientException {

		HttpResponse response = null;
		int code = HttpStatus.SC_OK;
		final SproxydUrl base = urls.getNext();
		try
		{
			final String url = String.format("%s%s/%s", base, container, object);
			final HttpGet get = HttpClientUtil.makeHttpGet(url);
			request = get;
			response = client.execute(get);

			final StatusLine statusLine = response.getStatusLine();
			code = statusLine.getStatusCode();
			if (code == HttpStatus.SC_OK) {
				final InputStream content = response.getEntity().getContent();
				response = null;
				return content;
			} else if (code == HttpStatus.SC_NOT_FOUND) {
				final InputStream content = response.getEntity().getContent();
				response = null;
				return content;
			}
		} catch (IOException exc) {
			base.exclude(exc.getMessage());
			throw exc;
		} finally {
			if (response != null)
				EntityUtils.consume(response.getEntity());
		}
		if (code == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
			//base.exclude(statusLine.getReasonPhrase());
		}
		throw new SproxydClientException(code, "failed to read object");
	}

	public void storeStreamedObject(String container, String object,
			InputStream data, long length) throws IOException, SproxydClientException {
		HttpResponse response = null;
		int code = HttpStatus.SC_OK;
		final SproxydUrl base = urls.getNext();
		int trials = 3;
		while (trials > 0) {
			try {
				final String url = String.format("%s%s/%s", base, container, object);
				final HttpPut put = HttpClientUtil.makeHttpPut(url);
				request = put;
				final InputStreamEntity entity = new InputStreamEntity(data, length);
				entity.setContentType("application/octet-stream");
				if (length < 0) {
					entity.setChunked(true);
				}
				put.setEntity(entity);
				response = client.execute(put);
				final StatusLine statusLine = response.getStatusLine();
				code = statusLine.getStatusCode();
			} catch (IOException exc) {
				base.exclude(exc.getMessage());
				throw exc;
			} finally {
				if (response != null)
					EntityUtils.consume(response.getEntity());
			}
			// retry locked and internal server errors when allowed to
			if ((code != HttpStatus.SC_INTERNAL_SERVER_ERROR) && (code != HttpStatus.SC_LOCKED)) {
				break;
			}
			final Header[] retryAllowed = response.getHeaders("X-Scal-Retry-Allowed");
			if ((code == HttpStatus.SC_INTERNAL_SERVER_ERROR) && (retryAllowed.length > 0) && retryAllowed[0].equals("No")) {
				break;
			}
			if (code == HttpStatus.SC_LOCKED) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					throw new SproxydClientException(code, e.getLocalizedMessage());
				}
			}
			trials--;
		}
		if ((code != HttpStatus.SC_CREATED) && (code != HttpStatus.SC_OK)) {
			throw new SproxydClientException(code, "failed to put object");
		}
	}

	public void deleteObject(String container, String object)
			throws IOException, SproxydClientException {
		HttpResponse response = null;
		int code = HttpStatus.SC_OK;
		final SproxydUrl base = urls.getNext();
		try {
			final String url = String.format("%s%s/%s", base, container, object);
			final HttpDelete delete = HttpClientUtil.makeHttpDelete(url);
			request = delete;
			response = client.execute(delete);
			final StatusLine statusLine = response.getStatusLine();
			code = statusLine.getStatusCode();
		} catch (IOException exc) {
			base.exclude(exc.getMessage());
			throw exc;
		} finally {
			if (response != null)
				EntityUtils.consume(response.getEntity());
		}
		if (code == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
			//base.exclude(statusLine.getReasonPhrase());
		}
		if (code != HttpStatus.SC_OK) {
			throw new SproxydClientException(code, "failed to delete object");
		}
	}

	/**
	 * Key of an sproxyd server (host + port) for blacklisting purposes.
	 *
	 */
	private static class Endpoint {
		private final String host;
		private final int port;

		Endpoint(URI uri) {
			this.host= uri.getHost();
			this.port = uri.getPort();
		}

		public String getHost() {
			return host;
		}

		public int getPort() {
			return port;
		}

		@Override
		public int hashCode() {
			return getHost().hashCode() + getPort();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Endpoint) {
				final Endpoint other = (Endpoint)obj;
				return (getPort() == other.getPort()) && getHost().equals(other.getHost());
			}
			return false;
		}

		@Override
		public String toString() {
			return getHost()+":"+getPort();
		}
	}

	/**
	 * Background task that monitors an endpoint for blacklisting.
	 *
	 */
	private static class SproxydEndpoint  extends Thread {
		private static int id = 0;
		private static int checkPeriod = DEFAULT_CHECK_PERIOD;
		private final URI uri;
		private HttpClient client;
		private boolean isUp;
		private volatile boolean shutdownRequested = false;

		SproxydEndpoint(URI uri) {
			this.uri = uri;
			this.client = HttpClientUtil.createHttpClient(300);
			this.setName(String.format("ScalConnChecker-%d", id++));
			this.setDaemon(true);
			isUp = true;
		}

		public synchronized boolean isUp() {
			return isUp;
		}

		public synchronized boolean setUp(boolean isUp) {
			final boolean before = this.isUp;
			this.isUp = isUp;
			return before;
		}

		private void update(boolean isUp, String message) {
			boolean wasUp = setUp(isUp);
			if (wasUp && !isUp) {
				warn("Blacklisting {}, {}", uri, message);

			} else if (!wasUp) {
				if (isUp) {
					warn("{} is up", uri);
				} else {
					warn("{} still down", uri);
				}
			}
		}

		public void exclude(String message) {
			update(false, message);
		}

		public void include() {
			update(true, "");
		}

		public void retryConnection() {
			if (isUp()) {
				//LOG.debug("Checking connection to {}", uri.toString());
				return;
			} else {
				info("Checking connection to {}", uri.toString());
			}
			HttpResponse response = null;
			final HttpGet get = HttpClientUtil.makeHttpGet(uri.toString() + ".conf");
			try {
				response = client.execute(get);
				final StatusLine statusLine = response.getStatusLine();
				final int statusCode = statusLine.getStatusCode();
				if (statusCode == HttpStatus.SC_OK) {
					include();
				} else if (statusCode >= HttpStatus.SC_INTERNAL_SERVER_ERROR) {
					exclude(statusLine.getReasonPhrase());
				}
			} catch (Exception e) {
				exclude(e.getMessage());
			}
		}

		@SuppressWarnings("unused")
		public void shutdown() {
			shutdownRequested = true;
			this.interrupt();
		}

		@Override
		public void run() {
			info("{} thread starting", getName());
			while (!shutdownRequested) {
				retryConnection();
				try {
					Thread.sleep(checkPeriod);
				} catch (InterruptedException e) {
				}
			}
			info("{} thread exiting", getName());
		}
	}

	static class SproxydUrl {
		private final URI uri;
		private SproxydEndpoint endpoint;

		SproxydUrl(String url) throws URISyntaxException {
			if (!url.endsWith("/")) {
				url += "/";
			}
			uri = new URI(url);
		}

		public boolean isUp() {
			return endpoint.isUp();
		}

		public void exclude(String message) {
			endpoint.exclude(message);
		}

		public void registerEndpoint(final Map<Endpoint, SproxydEndpoint> endpoints) {
			final Endpoint key = new Endpoint(uri);
			if (endpoints.containsKey(key)) {
				endpoint = endpoints.get(key);
			} else {
				endpoint = new SproxydEndpoint(uri);
				endpoints.put(key, endpoint);
			}
		}

		@Override
		public String toString() {
			return uri.toString();
		}
	}

	private static class SproxydUrlList {
		private SproxydUrl urls[];
		private int current = 0;

		SproxydUrlList(String hosts, int port, String basePath) {
			final String[] hostList = hosts.split(",");
			urls = new SproxydUrl[hostList.length];
			for (int i=0; i< hostList.length; i++) {
				try {
					urls[i] = new SproxydUrl(String.format("http://%s:%d%s", hostList[i], port, basePath));
				} catch (URISyntaxException e) {
				}
			}
		}

		void registerEndpoints(final Map<Endpoint, SproxydEndpoint> endpoints) {
			for (SproxydUrl url : urls) {
				url.registerEndpoint(endpoints);
			}
		}

		synchronized SproxydUrl getNext() throws SproxydClientException {
			int trials = urls.length;
			while (trials > 0) {
				current = (current + 1) % urls.length;
				final SproxydUrl url = urls[current];
				if (url.isUp()) return url;
				trials -= 1;
			}
			throw new SproxydClientException(0, "All servers are blacklisted");
		}

		@Override
		public String toString() {
			return Arrays.toString(urls);
		}

	}

	private static void info(String format, Object arg) {
		if (LOG != null) LOG.info(format, arg);
	}

	private static void warn(String format, Object arg) {
		if (LOG != null) LOG.warn(format, arg);
	}

	private static void warn(String format, Object arg1, Object arg2) {
		if (LOG != null) LOG.warn(format, arg1, arg2);
	}

}
