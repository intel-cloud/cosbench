/*
 * Copyright 2014-2016 EMC Corporation. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.emc.vipr.cosbench.ECSStor;

import com.emc.object.Protocol;
import com.emc.object.Range;
import com.emc.object.s3.S3Client;
import com.emc.object.s3.S3Config;
import com.emc.object.s3.S3ObjectMetadata;
import com.emc.object.s3.jersey.S3JerseyClient;
import com.emc.object.s3.request.PutObjectRequest;
import com.intel.cosbench.api.context.AuthContext;
import com.intel.cosbench.api.storage.NoneStorage;
import com.intel.cosbench.config.Config;
import com.intel.cosbench.log.Logger;
import com.sun.jersey.client.urlconnection.URLConnectionClientHandler;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * COSBench Storage API for EMC ECS Object Storage interface.
 * Enables use of ECS smart client for load balancing.
 */
public class ECSStorage extends NoneStorage
        implements ECSConstants {

    //Local environment variables
    private String connTimeout;
    private String readTimeout;
    private String accessKey;
    private String secretKey;
    private String endpoint;
    private String httpClient;
    private boolean smartClient;
    protected S3Client s3Client;
    S3Config s3CliConfig;

    /**
     * Empty constructor; does nothing.
     */
    public ECSStorage() {
    }

    /**
     * Initialize this storage API using the provided congifuration
     * and logger.
     *
     * @param config configuration to be used for this operation
     * @param logger logger instance to for log information output
     */
    public void init(Config config, Logger logger) {

        super.init(config, logger);

        //Initialize environment variables from config
        connTimeout = config.get(CONN_TIMEOUT_KEY, CONN_TIMEOUT_DEFAULT);
        if (config.get(READ_TIMEOUT_KEY, "") != "") {
            readTimeout = config.get(READ_TIMEOUT_KEY, "");
        }
        else readTimeout = connTimeout;
        parms.put(CONN_TIMEOUT_KEY, connTimeout);
        parms.put(READ_TIMEOUT_KEY, readTimeout);

        endpoint = config.get(ENDPOINT_KEY, ENDPOINT_DEFAULT);
        accessKey = config.get(AUTH_USERNAME_KEY, AUTH_USERNAME_DEFAULT);
        secretKey = config.get(AUTH_PASSWORD_KEY, AUTH_PASSWORD_DEFAULT);
        boolean pathStyleAccess = config.getBoolean(PATH_STYLE_ACCESS_KEY, PATH_STYLE_ACCESS_DEFAULT);
//        String proxyHost = config.get(PROXY_HOST_KEY, "");
//        String proxyPort = config.get(PROXY_PORT_KEY, "");
        String namespace = config.get(NAMESPACE_KEY, "");
        httpClient = config.get(HTTP_CLIENT_KEY, HTTP_CLIENT_DEFAULT);
        smartClient = config.getBoolean(SMART_CLIENT_KEY, SMART_CLIENT_DEFAULT);

        //Put new info. to environment variables where applicable
        parms.put(ENDPOINT_KEY, endpoint);
        parms.put(AUTH_USERNAME_KEY, accessKey);
        parms.put(AUTH_PASSWORD_KEY, secretKey);
        parms.put(PATH_STYLE_ACCESS_KEY, pathStyleAccess);
//        parms.put(PROXY_HOST_KEY, proxyHost);
//        parms.put(PROXY_PORT_KEY, proxyPort);
        parms.put(NAMESPACE_KEY, namespace);
        parms.put(HTTP_CLIENT_KEY, httpClient);
        parms.put(SMART_CLIENT_KEY, smartClient);
        logger.debug("using storage config: {}", parms);

        String[] hostArr = endpoint.split(",");
        for (int i = 0; i < hostArr.length; i++) {
            int stInd = hostArr[i].indexOf("/") + 2;    //Starting index of endpoint IP
            hostArr[i] = hostArr[i].substring(stInd, hostArr[i].indexOf(":", stInd + 1));
        }
        if ( smartClient == Boolean.FALSE ) try {
            s3CliConfig = new S3Config(new URI(hostArr[0]));
        } catch (URISyntaxException e) {
            logger.error(e.getMessage());
        }
        else {
            try {
                if (endpoint.toLowerCase().contains("https")) s3CliConfig = new S3Config(Protocol.HTTPS, hostArr);
                else s3CliConfig = new S3Config(Protocol.HTTP, hostArr);
            } catch (Exception e) {
                logger.error(e.getMessage());
                s3CliConfig = new S3Config(Protocol.HTTPS, hostArr);
            }
        }
        s3CliConfig.withIdentity(accessKey).withSecretKey(secretKey);


        try {
            System.setProperty("http.maxConnections", Integer.toString(Thread.activeCount()) + 128);
            if(!connTimeout.equals("")) System.setProperty("com.sun.jersey.client.property.readTimeout", connTimeout);
            if(!readTimeout.equals("")) System.setProperty("com.sun.jersey.client.property.connectTimeout", readTimeout);
        }
        catch (Exception e) {
            logger.error(e.getMessage());
            //throw new StorageException(e);
        }

        if (!namespace.equals("")) s3CliConfig.setNamespace(namespace);

        if (httpClient == "java") {
            s3Client =  new S3JerseyClient(s3CliConfig, new URLConnectionClientHandler());
        }
        else{
            s3Client = new S3JerseyClient(s3CliConfig);
        }

    }

    /**
     * Set auth context for this storage API.
     *
     * @param info AuthContext for this storage API.
     */
    public void setAuthContext(AuthContext info) {
        super.setAuthContext(info);
    }

    /**
     * Release held client resource.
     */
    public void dispose() {
        super.dispose();
        s3Client.destroy();
        s3Client = null;
    }


    /**
     * Retrieves an object from a ECS bucket as an InputStream.
     *
     * @param container bucket containing the desired object.
     * @param object    key (identifier) of object to be returned
     * @param config    configuration used for this operation
     * @return inputStream an InputStream representing the content
     * of the object; if null, the object was not found
     */
    public InputStream getObject(String container, String object, Config config) {

        super.getObject(container, object, config);
        InputStream stream;

        try {
            logger.info((new StringBuilder("Retrieving ")).append(container).append("\\").append(object).toString());
            stream = s3Client.readObjectStream(container, object, new Range(new Long(0), s3Client.getObjectMetadata(container, object).getContentLength()));
        } catch (Exception e) {
            logger.error(e.getMessage());
            stream = null;
            //throw new StorageException(e);
        }
        return stream;
    }

    /**
     * Creates a bucket using the ECS client
     *
     * @param container name of bucket to be created
     * @param config    configuration used for this operation
     */
    public void createContainer(String container, Config config) {

        super.createContainer(container, config);

        try {
            if (!s3Client.bucketExists(container)) {
                logger.info((new StringBuilder("Creating ")).append(container).toString());
                s3Client.createBucket(container);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            //throw new StorageException(e);
        }
    }

    /**
     * Creates an object using the ECS client
     *
     * @param container name of bucket containing the new object
     * @param object    key (identifier) of object to be created
     * @param data      content of the new object
     * @param length    length of the new object; stored in metadata
     * @param config    configuration used for this operation
     */
    public void createObject(String container, String object, InputStream data, long length, Config config) {

        super.createObject(container, object, data, length, config);

        try {
            logger.info((new StringBuilder("Creating ")).append(container).append("\\").append(object).append(" with length=").append(length).append(" Bytes").toString());
            PutObjectRequest req = new PutObjectRequest(container, object, data).withObjectMetadata(new S3ObjectMetadata().withContentLength(length));
            s3Client.putObject(req);
        } catch (Exception e) {
            logger.error(e.getMessage());
            //throw new StorageException(e);
        }
    }

    /**
     * Deletes a bucket using the ECS client
     *
     * @param container name of bucket to be deleted
     * @param config    configuration used for this operation
     */
    public void deleteContainer(String container, Config config) {

        super.deleteContainer(container, config);

        try {
            if (s3Client.bucketExists(container)) {
                logger.info((new StringBuilder("Deleting ")).append(container).toString());
                s3Client.deleteBucket(container);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            //throw new StorageException(e);
        }
    }

    /**
     * Deletes an object using the ECS client
     *
     * @param container name of bucket containing object
     * @param object    key (identifier) of object to be deleted
     * @param config    configuration used for this operation
     */
    public void deleteObject(String container, String object, Config config) {

        super.deleteObject(container, object, config);

        try {
            logger.info((new StringBuilder("Deleting ")).append(container).append("\\").append(object).toString());
            s3Client.deleteObject(container, object);
        } catch (Exception e) {

            logger.error(e.getMessage());
            //throw new StorageException(e);
        }
    }

}