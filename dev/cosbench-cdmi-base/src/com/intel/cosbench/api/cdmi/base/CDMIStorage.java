package com.intel.cosbench.api.cdmi.base;

import static com.intel.cosbench.client.cdmi.base.CdmiConstants.*;
import java.io.*;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.HttpClient;
import com.intel.cosbench.api.storage.*;
import com.intel.cosbench.api.context.*;
import com.intel.cosbench.client.cdmi.base.*;
import com.intel.cosbench.client.http.HttpClientUtil;
import com.intel.cosbench.config.Config;
import com.intel.cosbench.log.Logger;

public class CDMIStorage extends NoneStorage {

	// below parameters expect to get from auth module.
    private HttpClient httpClient;
    private String url;

    // below parameters expect to get from configuration file.
    private int timeout;
    private String rootPath;
    private String type; 
    private String headers;
    private boolean flag;
    
    // local variables
    private BaseCdmiClient client;
    private String[] header_list;
    
    @Override
    public void init(Config config, Logger logger) {
        super.init(config, logger);
        initParms(config);

        client = CdmiClientFactory.getClient(type);
    }

    private void initParms(Config config) {
    	rootPath = config.get(ROOT_PATH_KEY, ROOT_PATH_DEFAULT);
        timeout = config.getInt(TIMEOUT_KEY, TIMEOUT_DEFAULT);
        headers = config.get(CUSTOM_HEADERS_KEY, CUSTOM_HEADERS_DEFAULT);
        flag = config.getBoolean(RAISE_DELETE_ERRORS_KEY, RAISE_DELETE_ERRORS_DEFAULT);
        type = config.get(CDMI_CONTENT_TYPE_KEY, CDMI_CONTENT_TYPE_DEFAULT);
        header_list = headers.split(",");
                
        parms.put(ROOT_PATH_KEY, rootPath);
    	parms.put(TIMEOUT_KEY, timeout);
    	parms.put(RAISE_DELETE_ERRORS_KEY, flag);    	
    	parms.put(CDMI_CONTENT_TYPE_KEY, type);
    }

    @Override
    public void setAuthContext(AuthContext info) {
        super.setAuthContext(info);
        try {
        	httpClient = (HttpClient) info.get(AUTH_CLIENT_KEY);
        	if(httpClient == null) // client is set
        		httpClient = HttpClientUtil.createHttpClient(timeout);
            url = info.getStr(STORAGE_URL_KEY) + rootPath;            
            
            // subtitute headers
            // "headers=X-AUTH-TOKEN:;"
            Map<String, String> headerKV = new HashMap<String, String>();
            for(String header : header_list) {
            	String[] kv = header.split(":");
            	if (kv.length >= 2) {
            		headerKV.put(kv[0], info.getStr(kv[1]));
            	}
            }
                        
            logger.debug("httpclient =" + httpClient + ", url = " + url);
            client.init(httpClient, url, headerKV, false);
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
        } catch (CdmiClientException se) {
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
        } catch (CdmiClientException se) {
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
        } catch (CdmiClientException se) {
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
        } catch (CdmiClientException se) {
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
        } catch (CdmiClientException se) {
            String msg = se.getHttpStatusLine().toString();
            throw new StorageException(msg, se);
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

}
