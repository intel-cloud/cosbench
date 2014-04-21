package com.intel.cosbench.driver.generator;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.input.CountingInputStream;

/**
 * This class is to record the time of data transfer
 * 
 * 
 */

public class XferCountingInputStream extends CountingInputStream{
	private long xferStart = 0L;
	private long xferEnd = 0L;
	private boolean isFirstByte = true;
	
	public XferCountingInputStream(InputStream in) {
		super(in);	
	}
	
	@Override
	public int read() throws IOException {
		int result = super.read();
		recordTime();
		return result;
	}
	
	@Override
	public int read(byte[] b) throws IOException {
		int result = super.read(b);
		recordTime();
		return result;
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int result = super.read(b, off, len);
		recordTime();
		return result;
	}
	
	private void recordTime() {
		if (this.isFirstByte) {
			this.xferStart = System.currentTimeMillis();
			this.isFirstByte = false;
		}
		this.xferEnd = System.currentTimeMillis();
	}
	
	public long getXferTime() {
		long xferTime = this.xferEnd - this.xferStart;
		return xferTime > 0 ? xferTime : 0L;
	}
	
}
