/**

Copyright 2013 Intel Corporation, All Rights Reserved.
Copyright 2019 OpenIO Corporation, All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

*/
package com.intel.cosbench.api.oss;

import static com.intel.cosbench.client.oss.OSSConstants.AUTH_PASSWORD_DEFAULT;
import static com.intel.cosbench.client.oss.OSSConstants.AUTH_PASSWORD_KEY;
import static com.intel.cosbench.client.oss.OSSConstants.AUTH_USERNAME_DEFAULT;
import static com.intel.cosbench.client.oss.OSSConstants.AUTH_USERNAME_KEY;
import static com.intel.cosbench.client.oss.OSSConstants.CONN_TIMEOUT_DEFAULT;
import static com.intel.cosbench.client.oss.OSSConstants.CONN_TIMEOUT_KEY;
import static com.intel.cosbench.client.oss.OSSConstants.ENDPOINT_DEFAULT;
import static com.intel.cosbench.client.oss.OSSConstants.ENDPOINT_KEY;
import static com.intel.cosbench.client.oss.OSSConstants.PATH_STYLE_ACCESS_DEFAULT;
import static com.intel.cosbench.client.oss.OSSConstants.PATH_STYLE_ACCESS_KEY;
import static com.intel.cosbench.client.oss.OSSConstants.PROXY_HOST_KEY;
import static com.intel.cosbench.client.oss.OSSConstants.PROXY_PORT_KEY;

import java.io.InputStream;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.auth.Credentials;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.auth.DefaultCredentials;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.intel.cosbench.api.context.AuthContext;
import com.intel.cosbench.api.context.Context;
import com.intel.cosbench.api.storage.NoneStorage;
import com.intel.cosbench.api.storage.StorageException;
import com.intel.cosbench.config.Config;
import com.intel.cosbench.log.Logger;

public class OSSStorage extends NoneStorage {

    private int timeout;
    private String accessKey;
    private String secretKey;
    private String endpoint;

    private OSS ossClient;

    @Override
    public void init(Config config, Logger logger) {
        super.init(config, logger);

        timeout = config.getInt(CONN_TIMEOUT_KEY, CONN_TIMEOUT_DEFAULT);

        parms.put(CONN_TIMEOUT_KEY, timeout);

        endpoint = config.get(ENDPOINT_KEY, ENDPOINT_DEFAULT);
        accessKey = config.get(AUTH_USERNAME_KEY, AUTH_USERNAME_DEFAULT);
        secretKey = config.get(AUTH_PASSWORD_KEY, AUTH_PASSWORD_DEFAULT);

        boolean pathStyleAccess = config.getBoolean(PATH_STYLE_ACCESS_KEY, PATH_STYLE_ACCESS_DEFAULT);

        String proxyHost = config.get(PROXY_HOST_KEY, "");
        String proxyPort = config.get(PROXY_PORT_KEY, "");

        parms.put(ENDPOINT_KEY, endpoint);
        parms.put(AUTH_USERNAME_KEY, accessKey);
        parms.put(AUTH_PASSWORD_KEY, secretKey);
        parms.put(PATH_STYLE_ACCESS_KEY, pathStyleAccess);
        parms.put(PROXY_HOST_KEY, proxyHost);
        parms.put(PROXY_PORT_KEY, proxyPort);

        logger.debug("using storage config: {}", parms);

        ClientConfiguration clientConf = new ClientConfiguration();
        clientConf.setConnectionTimeout(timeout);
        clientConf.setSocketTimeout(timeout);
        // clientConf.setProtocol(Protocol.HTTP);
        if ((!proxyHost.equals("")) && (!proxyPort.equals(""))) {
            clientConf.setProxyHost(proxyHost);
            clientConf.setProxyPort(Integer.parseInt(proxyPort));
        }

        Credentials ossCredentials = new DefaultCredentials(accessKey, secretKey);
        CredentialsProvider credsProvider = new DefaultCredentialProvider(ossCredentials);
        ossClient = new OSSClient(endpoint, credsProvider, clientConf);

        logger.debug("aliyun oss client has been initialized");
    }

    @Override
    public void setAuthContext(AuthContext info) {
        super.setAuthContext(info);
    }

    @Override
    public void dispose() {
        super.dispose();
        ossClient = null;
    }

    @Override
    public Context getParms() {
        return super.getParms();
    }

    @Override
    public InputStream getObject(String container, String object, Config config) {
        super.getObject(container, object, config);
        InputStream stream;
        try {
            OSSObject ossObject = ossClient.getObject(container, object);
            stream = ossObject.getObjectContent();
        } catch (OSSException ossExce) {
            throw new StorageException("error message:" + ossExce.getErrorMessage(), ossExce);
        } catch (Exception e) {
            throw new StorageException(e);
        }
        return stream;
    }

    @Override
    public void createContainer(String container, Config config) {
        super.createContainer(container, config);
        try {
            if (!ossClient.doesBucketExist(container)) {
                ossClient.createBucket(container);
            }
        } catch (OSSException ossExce) {
            throw new StorageException(ossExce.getErrorMessage(), ossExce);
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public void createObject(String container, String object, InputStream data, long length, Config config) {
        super.createObject(container, object, data, length, config);
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(length);
            metadata.setContentType("application/octet-stream");
            ossClient.putObject(container, object, data, metadata);
        } catch (OSSException ossExce) {
            throw new StorageException(ossExce.getErrorMessage(), ossExce);
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public void deleteContainer(String container, Config config) {
        super.deleteContainer(container, config);
        try {
            if (ossClient.doesBucketExist(container)) {
                ossClient.deleteBucket(container);
            }
        } catch (OSSException ossExce) {
            throw new StorageException(ossExce.getErrorMessage(), ossExce);
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public void deleteObject(String container, String object, Config config) {
        super.deleteObject(container, object, config);
        try {
            ossClient.deleteObject(container, object);
        } catch (OSSException ossExce) {
            throw new StorageException(ossExce.getErrorMessage(), ossExce);
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }
}
