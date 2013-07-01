package com.intel.cosbench.api.nioengine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

import org.apache.http.entity.ContentType;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.ContentDecoderChannel;
import org.apache.http.nio.FileContentDecoder;
import org.apache.http.util.Asserts;

/**
 * One consumer sink which uses one file to consume incoming data, so it means one external file will be created to store the data.
 * 
 * @author ywang19
 *
 */
public class ConsumerFileSink extends ConsumerSink<File> {
    private final RandomAccessFile accessfile;

    private FileChannel fileChannel;
    private long idx = -1;
	
	public ConsumerFileSink(File file) throws FileNotFoundException {
		super(file);
        this.accessfile = new RandomAccessFile(this.sink, "rw");
	}
	
	@Override
	public void close() {
        try {
            this.accessfile.close();
        } catch (final IOException ignore) {
        }
	}
	
	@Override
	public void connect(final ContentType contentType) {
		this.contentType = contentType;
        this.fileChannel = this.accessfile.getChannel();
        this.idx = 0;
	}
	
	@Override
	public void disconnect() throws IOException {		
        this.fileChannel.close();
        
		System.out.println("Total Bytes Transferred = " + idx);
	}
	
	@Override
	public void consume(final ContentDecoder decoder) throws IOException {		
        Asserts.notNull(this.fileChannel, "File channel");
        
		 long transferred;
	        if (decoder instanceof FileContentDecoder) {
	            transferred = ((FileContentDecoder)decoder).transfer(
	                    this.fileChannel, this.idx, Integer.MAX_VALUE);
	        } else {
	            transferred = this.fileChannel.transferFrom(
	                    new ContentDecoderChannel(decoder), this.idx, Integer.MAX_VALUE);
	        }
	        if (transferred > 0) {
	            this.idx += transferred;
	        }	

//			System.out.println("Byte Transferred = " + transferred);
	}
	
	@Override
	public ContentType getContentType() {
		return this.contentType;
	}

	@Override
	public File getSink() {
		return this.sink;
	}
}
