package com.intel.cosbench.client.http;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;

public class ZeroCopyFileConsumer extends ZeroCopyConsumer<File> {

	public ZeroCopyFileConsumer(File file) throws FileNotFoundException {
		super(file);
		// TODO Auto-generated constructor stub
	}

	protected File process(
	        final HttpResponse response,
	        final File file,
	        final ContentType contentType) throws Exception {
	    if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
	        throw new HttpException("Upload failed: " + response.getStatusLine());
	    }
	    return file;
	}
}
