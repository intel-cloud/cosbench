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
package com.intel.cosbench.api.S3Stor;

import static com.intel.cosbench.client.S3Stor.S3Constants.*;

import java.io.*;

import org.apache.http.HttpStatus;

import com.amazonaws.*;
import com.amazonaws.auth.*;
import com.amazonaws.services.s3.*;
import com.amazonaws.services.s3.model.*;

import com.intel.cosbench.api.storage.*;
import com.intel.cosbench.api.context.*;
import com.intel.cosbench.config.Config;
import com.intel.cosbench.log.Logger;

public class S3Storage extends NoneStorage {
    private int timeout;

    private String accessKey;
    private String secretKey;
    private String endpoint;

    private AmazonS3 client;

    @Override
    public void init(Config config, Logger logger) {
        super.init(config, logger);

        timeout = config.getInt(CONN_TIMEOUT_KEY, CONN_TIMEOUT_DEFAULT);

        parms.put(CONN_TIMEOUT_KEY, timeout);

        endpoint = config.get(ENDPOINT_KEY, ENDPOINT_DEFAULT);
        accessKey = config.get(AUTH_USERNAME_KEY, AUTH_USERNAME_DEFAULT);
        secretKey = config.get(AUTH_PASSWORD_KEY, AUTH_PASSWORD_DEFAULT);

        boolean pathStyleAccess = config.getBoolean(PATH_STYLE_ACCESS_KEY, PATH_STYLE_ACCESS_DEFAULT);
        int maxConnections = config.getInt(MAX_CONNECTIONS, MAX_CONNECTIONS_DEFAULT);

        String proxyHost = config.get(PROXY_HOST_KEY, "");
        String proxyPort = config.get(PROXY_PORT_KEY, "");

        parms.put(ENDPOINT_KEY, endpoint);
        parms.put(AUTH_USERNAME_KEY, accessKey);
        parms.put(AUTH_PASSWORD_KEY, secretKey);
        parms.put(PATH_STYLE_ACCESS_KEY, pathStyleAccess);
        parms.put(MAX_CONNECTIONS, maxConnections);
        parms.put(PROXY_HOST_KEY, proxyHost);
        parms.put(PROXY_PORT_KEY, proxyPort);

        initClient();
    }

    private AmazonS3 initClient() {
        logger.debug("initialize S3 client with storage config: {}", parms);

        ClientConfiguration clientConf = new ClientConfiguration();
        clientConf.setConnectionTimeout(parms.getInt(CONN_TIMEOUT_KEY));
        clientConf.setMaxConnections(parms.getInt(MAX_CONNECTIONS));
        clientConf.setSocketTimeout(timeout);
        clientConf.withUseExpectContinue(false);
        clientConf.withSignerOverride("S3SignerType");
        System.setProperty(SDKGlobalConfiguration.ENABLE_S3_SIGV4_SYSTEM_PROPERTY, "true");
        if((!parms.getStr(PROXY_HOST_KEY).equals(""))&&(!parms.getStr(PROXY_PORT_KEY).equals(""))){
            clientConf.setProxyHost(parms.getStr(PROXY_HOST_KEY));
            clientConf.setProxyPort(parms.getInt(PROXY_PORT_KEY));
        }

        AWSCredentials myCredentials = new BasicAWSCredentials(accessKey, secretKey);
        client = new AmazonS3Client(myCredentials, clientConf);
        client.setEndpoint(endpoint);
        client.setS3ClientOptions(new S3ClientOptions().withPathStyleAccess(parms.getBoolean(PATH_STYLE_ACCESS_KEY)));

        logger.debug("S3 client has been initialized");

        return client;
    }

    @Override
    public void setAuthContext(AuthContext info) {
        super.setAuthContext(info);
    }

    @Override
    public void dispose() {
        super.dispose();
        client = null;
    }

    @Override
    public InputStream getObject(String container, String object, Config config) {
        super.getObject(container, object, config);
        InputStream stream = null;
        try {

            S3Object s3Obj = client.getObject(container, object);
            stream = s3Obj.getObjectContent();

        } catch(AmazonServiceException ase) {
            if(ase.getStatusCode() != HttpStatus.SC_NOT_FOUND) {
                throw new StorageException(ase);
            }
        } catch (AmazonClientException ace) { // recreate the AmazonS3 client connection if it is broken.
            logger.warn("below exception encountered when retrieving object " + object + " at bucket " + container + ": " + ace.getMessage());
            ace.printStackTrace();
            initClient();
        }

        return stream;
    }

    @Override
    public void createContainer(String container, Config config) {
        super.createContainer(container, config);
        try {
            container = container.split("/")[0];
            if(!client.doesBucketExist(container)) {
                client.createBucket(container);
            }
        } catch(AmazonServiceException ase) {
            if(ase.getStatusCode() != HttpStatus.SC_NOT_FOUND) {
                throw new StorageException(ase);
            }
        } catch (AmazonClientException ace) { // recreate the AmazonS3 client connection if it is broken.
            logger.warn("below exception encountered when creating bucket " + container + ": " + ace.getMessage());
            ace.printStackTrace();
            initClient();
        }
    }

    @Override
    public void createObject(String container, String object, InputStream data,
            long length, Config config) {
        super.createObject(container, object, data, length, config);
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(length);
            metadata.setContentType("application/octet-stream");

            client.putObject(container, object, data, metadata);
        }catch(AmazonServiceException ase) {
            if(ase.getStatusCode() != HttpStatus.SC_NOT_FOUND) {
                throw new StorageException(ase);
            }
        } catch (AmazonClientException ace) { // recreate the AmazonS3 client connection if it is broken.
            logger.warn("below exception encountered when creating object " + object + " at " + container + ": " + ace.getMessage());
            ace.printStackTrace();
            initClient();
        }
    }

    @Override
    public void deleteContainer(String container, Config config) {
        super.deleteContainer(container, config);
        try {
            container = container.split("/")[0];
            if(client.doesBucketExist(container)) {
                client.deleteBucket(container);
            }
        } catch(AmazonServiceException ase) {
            if(ase.getStatusCode() != HttpStatus.SC_NOT_FOUND) {
                throw new StorageException(ase);
            }
        } catch (AmazonClientException ace) { // recreate the AmazonS3 client connection if it is broken.
            logger.warn("below exception encountered when deleting bucket " + container + ": " + ace.getMessage());
            ace.printStackTrace();
            initClient();
        }
    }

    @Override
    public void deleteObject(String container, String object, Config config) {
        super.deleteObject(container, object, config);
        try {
            client.deleteObject(container, object);
        } catch(AmazonServiceException ase) {
            if(ase.getStatusCode() != HttpStatus.SC_NOT_FOUND) {
                throw new StorageException(ase);
            }
        } catch (AmazonClientException ace) { // recreate the AmazonS3 client connection if it is broken.
            logger.warn("below exception encountered when deleting object " + object + " at bucket " + container + ": " + ace.getMessage());
            ace.printStackTrace();
            initClient();
        }
    }

    @Override
    public InputStream getList(String container, String object, Config config) {
        super.getList(container, object, config);
        InputStream stream = null;
        try {
            ObjectListing listing = client.listObjects(container, object);
            stream = new ByteArrayInputStream(listing.getObjectSummaries().toString().getBytes());
        } catch(AmazonServiceException ase) {
            if(ase.getStatusCode() != HttpStatus.SC_NOT_FOUND) {
                throw new StorageException(ase);
            }
        } catch (AmazonClientException ace) { // recreate the AmazonS3 client connection if it is broken.
            logger.warn("below exception encountered when listing object " + object + " at bucket " + container + ": " + ace.getMessage());
            ace.printStackTrace();
            initClient();
        }
        return stream;
    }
}
