package com.intel.cosbench.api.nioengine;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.impl.nio.pool.BasicNIOConnPool;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.nio.protocol.BasicAsyncRequestProducer;
import org.apache.http.nio.protocol.HttpAsyncRequester;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpProcessorBuilder;
import org.apache.http.protocol.RequestConnControl;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestExpectContinue;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;

/**
 * This class encapulates basic operations need for client to interact with NIO engine.
 * 
 * @author ywang19
 *
 */
public class NIOClient {

	private BasicNIOConnPool connPool;
	private HttpAsyncRequester requester;
	private CountDownLatch latch;
	
	public NIOClient(BasicNIOConnPool connPool)
	{
		this.connPool = connPool;
		latch = new CountDownLatch(connPool.getDefaultMaxPerRoute());
		
        HttpProcessor httpproc = HttpProcessorBuilder.create()
                // Use standard client-side protocol interceptors
                .add(new RequestContent())
                .add(new RequestTargetHost())
                .add(new RequestConnControl())
                .add(new RequestUserAgent("Mozilla/5.0"))
                .add(new RequestExpectContinue()).build();
        
        this.requester = new HttpAsyncRequester(httpproc);
	}
	
	public void await() throws InterruptedException
	{
		latch.await();
	}
	
    public void issueRequest(HttpHost target, HttpRequest request) throws Exception {
        // Create HTTP requester
//    	HttpHost proxy = new HttpHost("proxy-prc.intel.com", 911, "http");
        
        // Execute HTTP GETs to the following hosts and    	
        String path = "c:\\temp\\123.html";
    	COSBFutureCallback futureCallback =  new COSBFutureCallback(target, latch);
    	
    	long start = System.currentTimeMillis();
    	
    	HttpCoreContext coreContext = HttpCoreContext.create();
    	final ZCConsumer<File> consumer = new ZCConsumer<File>(new ConsumerFileSink(new File(path)));        	
//    	final ZCConsumer<ByteBuffer> consumer = new ZCConsumer<ByteBuffer>(new ConsumerNullSink(ByteBuffer.allocate(8192)));
   		
        Future<HttpResponse> future = requester.execute(
                new BasicAsyncRequestProducer(target, request),
                consumer,
//                new BasicAsyncResponseConsumer() ,
                connPool,
                coreContext,
                // Handle HTTP response from a callback
                futureCallback);
        
        if(future.isDone()) {
        	System.out.println("Request is done.");
        }
        	
//        future.get();
        
        long end = System.currentTimeMillis();
        
        System.out.println("Elapsed Time: " + (end-start) + " ms.");
    }


}
