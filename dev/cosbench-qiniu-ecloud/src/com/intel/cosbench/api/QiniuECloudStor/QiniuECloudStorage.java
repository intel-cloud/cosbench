package com.intel.cosbench.api.QiniuECloudStor;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.qiniu.ecloud.*;
import com.intel.cosbench.api.context.AuthContext;
import com.intel.cosbench.api.storage.NoneStorage;
import com.intel.cosbench.api.storage.StorageException;
import com.intel.cosbench.config.Config;
import com.intel.cosbench.log.Logger;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.qiniu.storage.UploadManager;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.http.Response;

import static com.intel.cosbench.client.QiniuECloudStor.QiniuECloudConstants.*;

public class QiniuECloudStorage extends NoneStorage {

	private Auth auth;
	private UploadManager uploadManager;
	private BucketManager bucketManager;
	private ECloudManager manager;
	private String ioHost;
	private String nid;
	private StringMap cachedBuckets;
	private StringMap cachedDomains;

	@Override
    public void init(Config config, Logger logger) {
    	super.init(config, logger);
        logger.debug("Qiniu ECloud client has been initialized");

        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
        String accessKey = config.get(AUTH_USERNAME_KEY, AUTH_USERNAME_DEFAULT);
        String secretKey = config.get(AUTH_PASSWORD_KEY, AUTH_PASSWORD_DEFAULT);
        auth = Auth.create(accessKey, secretKey);
        
        ioHost = config.get(QINIU_IO_HOST, QINIU_IO_HOST_DEFAULT);

        Zone zone = new Zone.Builder()
                .upHttp(config.get(QINIU_UP_HOST, QINIU_UP_HOST_DEFAULT)).build();

        Configuration c = new Configuration(zone);

        uploadManager = new UploadManager(c);
        bucketManager = new BucketManager(auth, c);
        manager = new ECloudManager(config.get(QINIU_ECLOUD_HOST, QINIU_ECLOUD_HOST_DEFAULT));

        String user = config.get(QINIU_ECLOUD_USERNAME, QINIU_ECLOUD_USERNAME_DEFAULT);
        String password = config.get(QINIU_ECLOUD_PASSWORD, QINIU_ECLOUD_PASSWORD_DEFAULT);
        try {
        	manager.login(user, password);
            ECloudRet.NamespacesRet r = manager.getNamespaces(null);
            if (r.items.length == 0) {
            	System.err.println("invalid user:" + user);
            }
            nid = r.items[0].id;
        } catch (ECloudException e) {
        	System.err.println(e);
        }
    }
    
    @Override
    public void setAuthContext(AuthContext info) {
        super.setAuthContext(info);
    }

    @Override
    public void dispose() {
        super.dispose();
        logger.debug("Qiniu ECloud client has been disposed");
    }

	@Override
    public InputStream getObject(String container, String object, Config config) {
        super.getObject(container, object, config);
        
        try {
        	URL url = new URL(String.format("http://%s/%s", ioHost, object));
        	HttpURLConnection c = (HttpURLConnection) url.openConnection();
        	String domain = cachedDomains.get(container).toString();
        	c.setRequestProperty("Host", domain);
        	return new BufferedInputStream(c.getInputStream());
        } catch (Exception e) {
        	throw new StorageException(e);
        }
    }

    @Override
    public void createContainer(String container, Config config) {
        super.createContainer(container, config);

        try {
			ECloudRet.Bucket b = manager.createBucket(nid, container, false);
			cachedBuckets.put(container, b.id);
			String domain = b.id+"_domain";
			manager.bindDomain(b.id, domain);
			cachedDomains.put(container, domain);
        } catch (Exception e) {
        	throw new StorageException(e);
        }
    }

	@Override
    public void createObject(String container, String object, InputStream data,
            long length, Config config) {
        super.createObject(container, object, data, length, config);
        
        String upToken = auth.uploadToken(container);
        if (length < 100000000) {
        	byte[] buffer = new byte[(int)length];
        	directUpload(buffer, object, upToken);
        	return;
        }
		try {
			uploadManager.put(data, object, upToken, null, null);
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
	
	private void directUpload(final byte[] data, final String key, String upToken) {
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

        try {
			String bid = cachedBuckets.get(container).toString();
			String domain = cachedDomains.get(container).toString();
			manager.unbindDomain(bid, domain);
			manager.deleteBucket(bid);
        } catch (Exception e) {
        	throw new StorageException(e);
        }
    }

    @Override
    public void deleteObject(String container, String object, Config config) {
        super.deleteObject(container, object, config);
        
        try {
            bucketManager.delete(container, object);
        } catch (QiniuException ex) {
            System.err.println(ex.code());
            System.err.println(ex.response.toString());
        }
    }
}
