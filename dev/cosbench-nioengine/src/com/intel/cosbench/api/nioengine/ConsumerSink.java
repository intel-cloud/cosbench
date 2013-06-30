package com.intel.cosbench.api.nioengine;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.ContentDecoder;

abstract class ConsumerSink<T> {
    protected HttpResponse response;
    protected ContentType contentType;
    protected T sink;
    
    
	abstract void disconnect() throws IOException;
	
	abstract void close();
	
	abstract void consume(ContentDecoder decoder) throws IOException;
	
	abstract void connect(ContentType contentType) throws IOException;
	
	public ConsumerSink(T sink) {
        if (sink == null) {
            throw new IllegalArgumentException("File may nor be null");
        }
        this.sink = sink;
	}
	
	ContentType getContentType() {
		return this.contentType; 
	}
	
	T getSink() {
		return this.sink;
	}
}
