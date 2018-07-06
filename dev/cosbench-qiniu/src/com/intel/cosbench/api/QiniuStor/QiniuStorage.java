package com.intel.cosbench.api.QiniuStor;

import java.io.*;
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
import com.qiniu.util.StringMap;

import static com.intel.cosbench.client.QiniuStor.QiniuConstants.*;

public class QiniuStorage extends NoneStorage {

    private UploadManager uploadManager;
    private BucketManager bucketManager;
    private Auth auth;
    private StringMap cachedDomains;

    @Override
    public void init(Config config, Logger logger)
    {
        super.init(config, logger);
        logger.debug("Qiniu client has been initialized");

        String accessKey = config.get(AUTH_USERNAME_KEY, AUTH_USERNAME_DEFAULT);
        String secretKey = config.get(AUTH_PASSWORD_KEY, AUTH_PASSWORD_DEFAULT);
        auth = Auth.create(accessKey, secretKey);
        Zone zone = Zone.zone0();
        Configuration c = new Configuration(zone);
        uploadManager = new UploadManager(c);
        bucketManager = new BucketManager(auth, c);
        cachedDomains = new StringMap();
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
        logger.debug("Qiniu client has been disposed");
    }

    @Override
    public InputStream getObject(String container, String object, Config config)
    {
        super.getObject(container, object, config);

        try {
            String domain = getDomain(container);
            String encodedFileName = URLEncoder.encode(object, "utf-8");
            URL url = new URL(String.format("http://%s/%s", domain, encodedFileName));
            HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
            return new BufferedInputStream(urlConnection.getInputStream());
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    private String getDomain(String bucket) throws Exception
    {
        Object domain = cachedDomains.get(bucket);
        if (domain != null) {
            return domain.toString();
        }
        String[] l = bucketManager.domainList(bucket);
        cachedDomains.put(bucket, l[0]);
        return l[0];
    }

    @Override
    public void createContainer(String container, Config config)
    {
        super.createContainer(container, config);

        try {
            bucketManager.createBucket(container, "z0");
        } catch (QiniuException e) {
            if (e.code() != 614) {
                throw new StorageException(e);
            }
        } catch (Exception e) {
            throw new StorageException(e);
        }
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
        // qiniu sdk does not support delete bucket
    }

    @Override
    public void deleteObject(String container, String object, Config config)
    {
        super.deleteObject(container, object, config);

        try {
            bucketManager.delete(container, object);
        } catch (QiniuException e) {
            throw new StorageException(e);
        }
    }
}
