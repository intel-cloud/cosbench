package com.intel.cosbench.client.http;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.ContentDecoderChannel;
import org.apache.http.nio.FileContentDecoder;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.protocol.AbstractAsyncResponseConsumer;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Asserts;

// ------------------------

public abstract class ZeroCopyConsumer<T> extends
		AbstractAsyncResponseConsumer<HttpResponse> {

	private final File file;
	private final RandomAccessFile accessfile;

	private HttpResponse response;
	private ContentType contentType;
	private FileChannel fileChannel;
	private long idx = -1;

	public ZeroCopyConsumer(final File file) throws FileNotFoundException {
		super();
		if (file == null) {
			throw new IllegalArgumentException("File may nor be null");
		}
		this.file = file;
		this.accessfile = new RandomAccessFile(this.file, "rw");
	}

	@Override
	protected void onResponseReceived(final HttpResponse response) {
		this.response = response;
	}

    
    @Override
    protected void onEntityEnclosed(
            final HttpEntity entity, final ContentType contentType) throws IOException {
        this.contentType = contentType;
        this.fileChannel = this.accessfile.getChannel();
        this.idx = 0;
    }

	@Override
	protected void onContentReceived(final ContentDecoder decoder,
			final IOControl ioctrl) throws IOException {
		Asserts.notNull(this.fileChannel, "File channel");
		long transferred;
		if (decoder instanceof FileContentDecoder) {
			transferred = ((FileContentDecoder) decoder).transfer(
					this.fileChannel, this.idx, Integer.MAX_VALUE);
		} else {
			transferred = this.fileChannel.transferFrom(
					new ContentDecoderChannel(decoder), this.idx,
					Integer.MAX_VALUE);
		}
		if (transferred > 0) {
			this.idx += transferred;
		}
		if (decoder.isCompleted()) {
			this.fileChannel.close();
		}
	}

	protected abstract T process(HttpResponse response, File file,
			ContentType contentType) throws Exception;

	
    @Override
    protected HttpResponse buildResult(final HttpContext context) throws Exception {
//        final FileEntity entity = new FileEntity(this.file);
//        entity.setContentType(this.response.getFirstHeader(HTTP.CONTENT_TYPE));
//        this.response.setEntity(entity);
//        process(this.response, this.file, this.contentType);

    	process(this.response, this.file, this.contentType);
    	
        return this.response;
    }


	@Override
	protected void releaseResources() {
		try {
			this.accessfile.close();
		} catch (final IOException ignore) {
		}
	}

}
