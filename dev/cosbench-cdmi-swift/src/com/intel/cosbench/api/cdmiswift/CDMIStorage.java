package com.intel.cosbench.api.cdmiswift;

import static com.intel.cosbench.client.cdmiswift.CdmiSwiftConstants.*;

import java.io.*;
import java.net.SocketTimeoutException;

import org.apache.http.client.HttpClient;

import com.intel.cosbench.api.storage.*;
import com.intel.cosbench.api.context.*;
import com.intel.cosbench.client.cdmiswift.*;
import com.intel.cosbench.client.http.HttpClientUtil;
import com.intel.cosbench.config.Config;
import com.intel.cosbench.log.Logger;

public class CDMIStorage extends NoneStorage {

    private CdmiSwiftClient client;
    private int timeout;
    
    private String rootPath;
    private String storageUrl;

    @Override
    public void init(Config config, Logger logger) {
        super.init(config, logger);
        initParms(config);
        
        HttpClient httpClient = HttpClientUtil.createHttpClient(timeout);
        client = new CdmiSwiftClient(httpClient);
    }

    private void initParms(Config config) {
    	rootPath = config.get(ROOT_PATH_KEY, ROOT_PATH_DEFAULT);
    	storageUrl = config.get(STORAGE_URL_KEY, STORAGE_URL_DEFAULT);
        timeout = config.getInt(TIMEOUT_KEY, TIMEOUT_DEFAULT);
        
        parms.put(ROOT_PATH_KEY, rootPath);
        parms.put(STORAGE_URL_KEY, storageUrl);
    	parms.put(TIMEOUT_KEY, timeout);
    }
    
//    @Override
//    public void setAuthContext(AuthContext info) {
//        super.setAuthContext(info);
//        try {
//        	String authToken = info.getStr(AUTH_TOKEN_KEY);
//            String tempUrl = info.getStr(STORAGE_URL_KEY);
//            String parts[] = tempUrl.split("/");
//            if(parts.length > 2)
//            {
//	            parts[parts.length-2] = nsroot;
//	            tempUrl = "";
//	            for(int i=0; i<parts.length; i++)
//	            	tempUrl += parts[i] + "/";
//            }
//            
//            logger.debug("auth token=" + authToken + ", storage url=" + tempUrl);
//            client.init(authToken, tempUrl);
//        } catch (Exception e) {
//            throw new StorageException(e);
//        }
//    }

    @Override
    public void setAuthContext(AuthContext info) {
        super.setAuthContext(info);
        try {
        	String authToken = info.getStr(AUTH_TOKEN_KEY);
            String storageUrl = info.getStr(STORAGE_URL_KEY);
            
            logger.debug("auth token=" + authToken + ", storage url=" + storageUrl);
            client.init(authToken, storageUrl);
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        client.dispose();
    }

    @Override
    public InputStream getObject(String container, String object, Config config) {
        super.getObject(container, object, config);
        InputStream stream;
        try {
            stream = client.getObjectAsStream(container, object);
        } catch (SocketTimeoutException te) {
            throw new StorageTimeoutException(te);
        } catch (InterruptedIOException ie) {
            throw new StorageInterruptedException(ie);
        } catch (CdmiSwiftClientException se) {
            String msg = se.getHttpStatusLine().toString();
            throw new StorageException(msg, se);
        } catch (Exception e) {
            throw new StorageException(e);
        }
        return stream;
    }

    @Override
    public void createContainer(String container, Config config) {
        super.createContainer(container, config);
        try {
            client.createContainer(container);
        } catch (SocketTimeoutException te) {
            throw new StorageTimeoutException(te);
        } catch (InterruptedIOException ie) {
            throw new StorageInterruptedException(ie);
        } catch (CdmiSwiftClientException se) {
            String msg = se.getHttpStatusLine().toString();
            throw new StorageException(msg, se);
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public void createObject(String container, String object, InputStream data,
            long length, Config config) {
        super.createObject(container, object, data, length, config);
        try {
            client.storeStreamedObject(container, object, data, length);
        } catch (SocketTimeoutException te) {
            throw new StorageTimeoutException(te);
        } catch (InterruptedIOException ie) {
            throw new StorageInterruptedException(ie);
        } catch (CdmiSwiftClientException se) {
            String msg = se.getHttpStatusLine().toString();
            throw new StorageException(msg, se);
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public void deleteContainer(String container, Config config) {
        super.deleteContainer(container, config);
        try {
            client.deleteContainer(container);
        } catch (SocketTimeoutException te) {
            throw new StorageTimeoutException(te);
        } catch (InterruptedIOException ie) {
            throw new StorageInterruptedException(ie);
        } catch (CdmiSwiftClientException se) {
            String msg = se.getHttpStatusLine().toString();
            throw new StorageException(msg, se);
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public void deleteObject(String container, String object, Config config) {
        super.deleteObject(container, object, config);
        try {
            client.deleteObject(container, object);
        } catch (SocketTimeoutException te) {
            throw new StorageTimeoutException(te);
        } catch (InterruptedIOException ie) {
            throw new StorageInterruptedException(ie);
        } catch (CdmiSwiftClientException se) {
            String msg = se.getHttpStatusLine().toString();
            throw new StorageException(msg, se);
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

}
