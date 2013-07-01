package com.intel.cosbench.driver.util;

import static org.apache.http.HttpStatus.SC_OK;

import org.apache.http.concurrent.FutureCallback;
import org.apache.http.HttpResponse;

public class HttpResponseCallback implements FutureCallback<HttpResponse> {

	private long start;
	private boolean status;
	private StatsCallback statsCallback;

	public HttpResponseCallback(long start, StatsCallback statsCallback) {
		this.start = start;
		this.statsCallback = statsCallback;
	}

	@Override
	public void cancelled() {
		status = false;
		long end = System.currentTimeMillis();
		statsCallback.onStats(null, end - start, status);
	}

	@Override
	public void completed(HttpResponse response) {
		if (response.getStatusLine().getStatusCode() == SC_OK) {
			status = true;
		} else {
			status = false;
		}
		long end = System.currentTimeMillis();
		// stats counting
		statsCallback.onStats(response, end - start, status);
	}

	@Override
	public void failed(Exception ex) {
		status = false;
		long end = System.currentTimeMillis();
		statsCallback.onStats(null, end - start, status);
	}

}
