package com.intel.cosbench.api.nioengine;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.protocol.AbstractAsyncResponseConsumer;
import org.apache.http.protocol.HttpContext;

/**
 * One zero-copy consumer, which can accept different consumer sink.
 * 
 * @author ywang19
 *
 * @param <T>
 */
public class ZCConsumer<T> extends AbstractAsyncResponseConsumer<HttpResponse> {

    private final ConsumerSink<T> sink;
    private HttpResponse response;
    
    public ZCConsumer(final ConsumerSink<T> sink) {
        super();
        
        this.sink = sink;
    }

    @Override
    protected void onResponseReceived(final HttpResponse response) {
        this.response = response;
    }

    
    @Override
    protected void onEntityEnclosed(
            final HttpEntity entity, final ContentType contentType) throws IOException {
        this.sink.connect(contentType);
    }

    @Override
    protected void onContentReceived(
            final ContentDecoder decoder, final IOControl ioctrl) throws IOException {
        this.sink.consume(decoder);
        
        if (decoder.isCompleted()) {
            this.sink.disconnect();
        }
    }

//    protected abstract T process(
//            HttpResponse response, ConsumerSink sink) throws Exception;

	
    @Override
    protected HttpResponse buildResult(final HttpContext context) throws Exception {
//        final FileEntity entity = new FileEntity(this.file);
//        entity.setContentType(this.response.getFirstHeader(HTTP.CONTENT_TYPE));
//        this.response.setEntity(entity);
//        process(this.response, this.sink);
        
	    if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
	        throw new HttpException("Upload failed: " + response.getStatusLine());
	    }
        
        return this.response;
    }

    @Override
    protected void releaseResources() {
    	this.sink.close();
    }

}

