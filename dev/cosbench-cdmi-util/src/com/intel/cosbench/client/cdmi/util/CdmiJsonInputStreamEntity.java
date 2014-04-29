package com.intel.cosbench.client.cdmi.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.TreeMap;

import org.apache.http.Header;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.message.BasicHeader;

/**
 * This class encapsulates an input stream which can handle the json structure adopted by cdmi content type.
 * 
 * @author ywang19
 * 
 */
public class CdmiJsonInputStreamEntity extends AbstractHttpEntity
{
	 private static final int BUFFER_SIZE = 2048;
	 private InputStream content;
	 private long length; // for value only in raw
	 private long transferLen; // for data actually transferred in entity
	 
	 private TreeMap<String, String> data = new TreeMap<String, String>() ;
	 private TreeMap<String, String> metadata = new TreeMap<String, String>();
	
	 public CdmiJsonInputStreamEntity()
	 {
		 this.content = null;
		 this.length = -1;
         this.transferLen = -1;
	 }

	 public CdmiJsonInputStreamEntity(InputStream instream, long length)
	 {
         content = instream;
         this.length = length;
         this.transferLen = -1;
	 }

	public boolean isRepeatable()
	 {
		if(content == null)
			return true;
		return content.markSupported();
	 }
	
	 public long getContentLength()
	 {
	     return transferLen;
	 }
	 
	 public long getTransferLength()
	 {
		 return transferLen;
	 }
	
	 public InputStream getContent()
	     throws IOException
	 {
	     return content;
	 }
	
	public void addMetadata(String key, String value) {
	    metadata.put(key, value);
	}
	
	public void addField(String key, String value) {
	    data.put(key, value);
	}
	
	public void addContent(InputStream content, long length) {
		this.content = content;
		this.length = length;
	}
	
	private String encodeEntity() {	    	
		data.put("metadata", encodeJson(metadata));
		
		if(content != null) {
			addField("mimetype", "text/plain");
			addField("valuetransferencoding", "utf-8");
		}
		
		return encodeJson(data);
	 }	 

	 private long writePre(OutputStream outstream) throws UnsupportedEncodingException, IOException {			    	
		StringBuffer buffer = new StringBuffer(encodeEntity());
		if(length > 0) {
			buffer.replace(buffer.length() - 1, buffer.length(), ",");
			buffer.append(" \"value\": \"");
		}
		byte[] bytes = buffer.toString().getBytes("utf-8");
		
		outstream.write(bytes);
				
		return bytes.length;
	 }
	 
	 private long writePost(OutputStream outstream) throws UnsupportedEncodingException, IOException {
		 if(length > 0) {
			 StringBuffer buffer = new StringBuffer("\" }");
			 byte[] bytes = buffer.toString().getBytes("UTF-8");
			 
			 outstream.write(bytes);
			 outstream.flush();
			 
			 return bytes.length;
		 }else {
			 outstream.flush();

			 return length;
		 }
	 }
	 
	public boolean isChunked()
	{
	    return false;
	}
	
	public Header getContentType()
	{
		return new BasicHeader("Content-Type", "application/cdmi-object");
	}	    
 
	 /**
	  * Write value stream to output stream
	  * 
	  * @param instream
	  * @param outstream
	  * @return
	  * @throws IOException
	  */
	 private long writeContent(InputStream instream, OutputStream outstream)
	 {
		 if(instream == null)
			 return 0L;
		 
	     byte buffer[] = new byte[BUFFER_SIZE];
		 int l;
	     long valueLen = 0;
	  
	     try{
		     if(length < 0L) {
		         while((l = instream.read(buffer)) != -1) {
		             outstream.write(buffer, 0, l);
		             valueLen += l;
		         }
		     } else {
		         long remaining = length;
		         do
		         {
		             if(remaining <= 0L)
		                 break;
		             l = instream.read(buffer, 0, (int)Math.min(BUFFER_SIZE, remaining));
		             if(l == -1)
		                 break;
		             outstream.write(buffer, 0, l);	
		             valueLen += l;
		             remaining -= l;
		         } while(true);
		     }
	     }catch(IOException e) {
	    	 e.printStackTrace();
	     }
	     
         return valueLen;
	 }
	 
	 public void writeTo(OutputStream outstream) throws IOException
	 {		 
		 transferLen = 0;
	     if(outstream == null)
	         throw new IllegalArgumentException("Output stream may not be null");
	     InputStream instream = content;
	     
	     try {
	    	 transferLen += writePre(outstream);		
    		 transferLen += writeContent(instream, outstream);
	         transferLen += writePost(outstream);
	     }finally {
			 if(instream != null)
				 instream.close();
	     }
	 }
	
	 public boolean isStreaming()
	 {
	     return true;
	 }
	
	 /**
	  * @deprecated Method consumeContent is deprecated
	  */
	
	 public void consumeContent()
	     throws IOException
	 {
	     content.close();
	 }
	 
	/**
	 * Encode all items except "value" into json format.
	 * 
	 * @param entries
	 * @return
	 */
	private static String encodeJson(Map<String, String> entries) {
		boolean hasValue = false;
	    StringBuffer buffer = new StringBuffer();
	    buffer.append('{');
		for (Map.Entry<String, String> item : entries.entrySet()) {
			// filter out "value"
			if (item.getKey().equals("value")) {
				continue;
			}
			
		    if (item.getKey().equals("metadata")) {
		        // json format in "metadata"
		        buffer.append(String.format(" \"%s\": %s,", item.getKey(), item.getValue()));
		    } else {
		        buffer.append(String.format(" \"%s\": \"%s\",", item.getKey(), item.getValue()));
		    }
		    hasValue = true;
		}
		if (hasValue) {
		    buffer.delete(buffer.length() - 1, buffer.length());
		}
		buffer.append(" }");
		
	    return buffer.toString();
	}

	public static void main(String[] args) {
		try{
			// entity without data value
//			System.out.println("=== Test Entity without value stream ===");
			FileOutputStream out_none = new FileOutputStream(new File("output_none.txt"));
			CdmiJsonInputStreamEntity entity_none = new CdmiJsonInputStreamEntity(null, 1000);
			entity_none.addMetadata("author", "ywang");
			entity_none.addMetadata("tag", "cdmi");			
			entity_none.writeTo(out_none);	
			
			// entity with data value
//			System.out.println("=== Test Entity with value stream from file ===");
			FileInputStream in_file = new FileInputStream(new File("in_file.txt"));
			FileOutputStream out_file = new FileOutputStream(new File("out_file.txt"));
			CdmiJsonInputStreamEntity entity_file = new CdmiJsonInputStreamEntity(in_file, 1024);	        
			entity_file.addMetadata("author", "ywang");
			entity_file.addMetadata("tag", "cdmi");
			entity_file.writeTo(out_file);				
			
			// entity with random input stream
//			System.out.println("=== Test Entity with value stream from random generator ===");
//	        RandomInputStream in_rnd = new RandomInputStream(100, new Random(23), true, false);
//			FileOutputStream out_rnd = new FileOutputStream(new File("output_rnd.txt"));
//			CdmiJsonInputStreamEntity entity_rnd = new CdmiJsonInputStreamEntity(in_rnd, 1024);	        
//			entity_rnd.addMetadata("author", "ywang");
//			entity_rnd.addMetadata("tag", "cdmi");
//			entity_rnd.writeTo(out_rnd);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
