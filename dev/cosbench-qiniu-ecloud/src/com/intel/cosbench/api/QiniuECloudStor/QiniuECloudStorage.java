package com.intel.cosbench.api.QiniuECloudStor;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import com.qiniu.ecloud.*;
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

import static com.intel.cosbench.client.QiniuECloudStor.QiniuECloudConstants.*;

public class QiniuECloudStorage extends NoneStorage {

    private Auth auth;
    private UploadManager uploadManager;
    private BucketManager bucketManager;
    private ECloudManager manager;
    private String ioHost;
    private String nid;

    @Override
    public void init(Config config, Logger logger)
    {
        super.init(config, logger);
        logger.debug("Qiniu ECloud client has been initialized");

        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
        String accessKey = config.get(AUTH_USERNAME_KEY, AUTH_USERNAME_DEFAULT);
        String secretKey = config.get(AUTH_PASSWORD_KEY, AUTH_PASSWORD_DEFAULT);
        auth = Auth.create(accessKey, secretKey);

        ioHost = config.get(QINIU_IO_HOST, QINIU_IO_HOST_DEFAULT);

        Zone zone = new Zone.Builder()
                        .upHttp(config.get(QINIU_UP_HOST, QINIU_UP_HOST_DEFAULT))
                        .upBackupHttp(config.get(QINIU_UP_HOST, QINIU_UP_HOST_DEFAULT))
                        .rsHttp(config.get(QINIU_RS_HOST, QINIU_RS_HOST_DEFAULT))
                        .build();

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
    public void setAuthContext(AuthContext info)
    {
        super.setAuthContext(info);
    }

    @Override
    public void dispose()
    {
        super.dispose();
        logger.debug("Qiniu ECloud client has been disposed");
    }

    @Override
    public InputStream getObject(String container, String object, Config config)
    {
        super.getObject(container, object, config);

        try {
            URL url = new URL(ioHost + "/" + URLEncoder.encode(object, "utf-8"));
            HttpURLConnection c = (HttpURLConnection)url.openConnection();
            String domain = container + "domain";
            c.setRequestProperty("Host", domain);
            return new BufferedInputStream(c.getInputStream());
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public void createContainer(String container, Config config)
    {
        super.createContainer(container, config);

        try {
            String bid = getBucket(nid, container);
            if (bid == null) {
                ECloudRet.Bucket b = manager.createBucket(nid, container, false);
                bid = b.id;
            }
            String domain = container + "domain";
            if (!domainExists(bid, domain)) {
                manager.bindDomain(bid, domain);
            }
        } catch (ECloudException e) {
            throw new StorageException(e);
        }
    }

    private String getBucket(String nid, String name)
    {
        try {
            ECloudRet.BucketsRet l = manager.getBuckets(nid, null);
            for (ECloudRet.Bucket b : l.items) {
                if (b.name.equals(name)) {
                    return b.id;
                }
            }
        } catch (ECloudException e) {
            // ignore
        }
        return null;
    }

    private boolean domainExists(String bid, String domain)
    {
        try {
            ECloudRet.Domain[] l = manager.getDomains(bid);
            for (ECloudRet.Domain d : l) {
                if (d.domain.equals(domain)) {
                    return true;
                }
            }
        } catch (ECloudException e) {
            // ignore
        }
        return false;
    }

    @Override
    public void createObject(String container, String object, InputStream data,
        long length, Config config)
    {
        super.createObject(container, object, data, length, config);

        String upToken = auth.uploadToken(container, object);
        createObj(object, data, length, upToken);
    }

    private void createObj(String key, InputStream data, long length,
        String upToken) throws StorageException
    {
        if (length <= 4 * 1024 * 1024) {
            byte[] buffer = new byte[(int)length];
            try {
                DataInputStream stream = new DataInputStream(data);
                stream.readFully(buffer);
                stream.close();
            } catch (Exception e) {
                throw new StorageException(e);
            }
            directUpload(buffer, key, upToken);
            return;
        }
        resumeUpload(data, key, upToken);
    }

    private void directUpload(final byte[] data, String key,
        String upToken) throws StorageException
    {
        try {
            uploadManager.put(data, key, upToken);
        } catch (QiniuException e) {
            throw new StorageException(e);
        }
    }

    private void resumeUpload(InputStream data, String key,
        String upToken) throws StorageException
    {
        try {
            uploadManager.put(data, key, upToken, null, null);
        } catch (QiniuException e) {
            throw new StorageException(e);
        }
    }

    @Override
    public void deleteContainer(String container, Config config)
    {
        super.deleteContainer(container, config);

        try {
            String bid = getBucket(nid, container);
            String domain = container + "domain";
            manager.unbindDomain(bid, domain);
            manager.deleteBucket(bid);
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public void deleteObject(String container, String object, Config config)
    {
        super.deleteObject(container, object, config);

        try {
            bucketManager.delete(container, object);
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }
}
