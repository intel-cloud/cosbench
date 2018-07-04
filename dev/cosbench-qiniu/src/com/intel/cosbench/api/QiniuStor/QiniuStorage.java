package com.intel.cosbench.api.QiniuStor;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import com.intel.cosbench.api.context.AuthContext;
import com.intel.cosbench.api.storage.NoneStorage;
import com.intel.cosbench.api.storage.StorageException;
import com.intel.cosbench.config.Config;
import com.intel.cosbench.log.Logger;
import com.qiniu.util.Auth;
import com.qiniu.storage.UploadManager;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.http.Response;
import java.io.IOException;

import static com.intel.cosbench.client.QiniuStor.QiniuConstants.*;

public class QiniuStorage extends NoneStorage {

	private UploadManager uploadManager;
	private BucketManager bucketManager;
	private String bucket;
	private String domain;
	private String upToken;

	@Override
    public void init(Config config, Logger logger) {
    	super.init(config, logger);
        logger.debug("Qiniu client has been initialized");
        
        bucket = config.get(QINIU_BUCKET_NAME, QINIU_BUCKET_NAME_DEFAULT);
        domain = config.get(QINIU_BUCKET_DOMAIN, QINIU_BUCKET_DOMAIN_DEFAULT);

        String accessKey = config.get(AUTH_USERNAME_KEY, AUTH_USERNAME_DEFAULT);
        String secretKey = config.get(AUTH_PASSWORD_KEY, AUTH_PASSWORD_DEFAULT);
        Auth auth = Auth.create(accessKey, secretKey);
        upToken = auth.uploadToken(bucket);
        
        Zone zone = new Zone.Builder()
                .upHttp(config.get(QINIU_UP_HOST, QINIU_UP_HOST_DEFAULT))
                .rsHttp(config.get(QINIU_RS_HOST, QINIU_RS_HOST_DEFAULT))
                .rsfHttp(config.get(QINIU_RSF_HOST, QINIU_RSF_HOST_DEFAULT))
                .apiHttp(config.get(QINIU_API_HOST, QINIU_API_HOST_DEFAULT))
                .iovipHttp(config.get(QINIU_IO_HOST, QINIU_IO_HOST_DEFAULT)).build();

        Configuration c = new Configuration(zone);

        uploadManager = new UploadManager(c);
        bucketManager = new BucketManager(auth, c);
    }
    
    @Override
    public void setAuthContext(AuthContext info) {
        super.setAuthContext(info);
    }

    @Override
    public void dispose() {
        super.dispose();
        logger.debug("Qiniu client has been disposed");
    }

	@Override
    public InputStream getObject(String container, String object, Config config) {
        super.getObject(container, object, config);

        try{
        	String key = container + "/" + object;
        	String encodedFileName = URLEncoder.encode(key, "utf-8");
        	URL url = new URL(String.format("http://%s/%s", domain, encodedFileName));
        	HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        	return new BufferedInputStream(urlConnection.getInputStream());
        } catch (Exception e) {
        	throw new StorageException(e);
        }
    }

    @Override
    public void createContainer(String container, Config config) {
        super.createContainer(container, config);
        //不创建
    }

	@Override
    public void createObject(String container, String object, InputStream data,
            long length, Config config) {
        super.createObject(container, object, data, length, config);
        
        String key = container + "/" + object;
        if (length < 100000000) {
        	byte[] buffer = new byte[(int)length];
        	directUpload(buffer, key);
        	return;
        }
		try {
			//调用put方法上传
			uploadManager.put(data, key, upToken, null, null);
		} catch (QiniuException e) {
			Response r = e.response;
			System.out.println(r.toString());
			try {
				System.out.println(r.bodyString());
			} catch (QiniuException e1) {
				//ignore
			}
		}
	}
	
	private void directUpload(final byte[] data, final String key) {
		try {
			uploadManager.put(data, key, upToken);
		} catch (QiniuException e) {
			Response r = e.response;
			System.out.println(r.toString());
			try {
				System.out.println(r.bodyString());
			} catch (QiniuException e1) {
				//ignore
			}
		}
	}

    @Override
    public void deleteContainer(String container, Config config) {
        super.deleteContainer(container, config);
        //不删除
    }

    @Override
    public void deleteObject(String container, String object, Config config) {
        super.deleteObject(container, object, config);
        
        String key = container + "/" + object;
        try {
            bucketManager.delete(bucket, key);
        } catch (QiniuException ex) {
            //如果遇到异常，说明删除失败
            System.err.println(ex.code());
            System.err.println(ex.response.toString());
        }
    }

}
