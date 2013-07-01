package com.intel.cosbench.api.nioengine;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.http.entity.ContentType;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.util.Asserts;

/**
 * One consumer sink which uses fix length buffer to consume incoming data.
 * 
 * @author ywang19
 *
 */
public class ConsumerBufferSink extends ConsumerSink<ByteBuffer> {
    private long idx = -1;
	
	public ConsumerBufferSink(ByteBuffer sink) {
		super(sink);
	}
	
	@Override
	public void close() {
    	this.sink.clear();
	}
	
	@Override
	public void connect(final ContentType contentType) {
		this.contentType = contentType;
        this.idx = 0;
	}
	
	@Override
	public void disconnect() throws IOException {	
		System.out.println("Total Bytes Transferred = " + idx);
	}
	
	@Override
	public void consume(final ContentDecoder decoder) throws IOException {		
        Asserts.notNull(this.sink, "Sink");

        long transferred = decoder.read(sink);
		if (transferred > 0) {
		    this.idx += transferred;
		}	
		
		sink.clear();
		System.out.println("Byte Transferred = " + transferred);
	}
	
	@Override
	public ByteBuffer getSink() {
		return this.sink;
	}
}
