/**

Copyright 2013 Intel Corporation, All Rights Reserved.
Copyright 2019 OpenIO Corporation, All Rights Reserved.
Copyright 2021 EHualu Corporation, All Rights Reserved.

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

import java.io.*;
import java.util.Random;

import java.util.List;
import java.util.ArrayList;

import com.amazonaws.*;
import com.amazonaws.auth.*;
import com.amazonaws.services.s3.*;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;

import com.intel.cosbench.log.Logger;
import com.intel.cosbench.api.storage.*;
import com.intel.cosbench.api.context.*;
import com.intel.cosbench.config.Config;
import static com.intel.cosbench.client.S3Stor.S3Constants.*;


public class S3Storage extends NoneStorage {

	private int timeout;

	private String accessKey;
	private String secretKey;
	private String endpoint;

	private AmazonS3 client;
	private AmazonS3 restoreClient; // add

	private boolean isPrefetch; 
    private boolean isRangeRequest; 
    private long fileLength;
    private long chunkLength;

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

		isPrefetch = config.getBoolean("is_prefetch", false);
		isRangeRequest = config.getBoolean("is_range_request", false);
		fileLength = config.getLong("file_length", 4000000L); // 4000000L = 4MB
		chunkLength = config.getLong("chunk_length", 1000000L); // 1000000L = 1MB

		parms.put(ENDPOINT_KEY, endpoint);
		parms.put(AUTH_USERNAME_KEY, accessKey);
		parms.put(AUTH_PASSWORD_KEY, secretKey);
		parms.put(PATH_STYLE_ACCESS_KEY, pathStyleAccess);
		parms.put(MAX_CONNECTIONS, maxConnections);
		parms.put(PROXY_HOST_KEY, proxyHost);
		parms.put(PROXY_PORT_KEY, proxyPort);

		// 2021.02.14
		// You can set storage_class to other value in storage part.
		String storageClass = config.get(STORAGE_CLASS_KEY, STORAGE_CLASS_DEFAULT);
		parms.put(STORAGE_CLASS_KEY, storageClass);

		// 2021.07.11
		// You can set restore_days to other value(int) in storage part.
		int restoreDays = config.getInt(RESTORE_DAYS_KEY, RESTORE_DAYS_DEFAULT);
		parms.put(RESTORE_DAYS_KEY, restoreDays);

		// 2021.08.03
		// You can set part_size to other value in storage part.
		long partSize = config.getLong(PART_SIZE_KEY, PART_SIZE_DEFAULT);
		parms.put(PART_SIZE_KEY, partSize);

		// 2020.11.26
		// You can set no_verify_ssl to true in storage part to disable SSL checking.
		boolean noVerifySSL = config.getBoolean(NO_VERIFY_SSL_KEY, NO_VERIFY_SSL_DEFAULT);
		if (noVerifySSL) {
			// This property is meant to be used as a flag
			// (i.e. -Dcom.amazonaws.sdk.disableCertChecking) rather then taking a value
			// (-Dcom.amazonaws.sdk.disableCertChecking=true).
			System.setProperty(SDKGlobalConfiguration.DISABLE_CERT_CHECKING_SYSTEM_PROPERTY, "true");
		}

		initClientV2();
		initRestoreClient();

	}

	// You can set different singType to get different client(common client type vs
	// restore client type). 2021.7.14��sine
	private ClientConfiguration getDefaultClientConfiguration(String signType) {

		ClientConfiguration defaultClientConfiguration = new ClientConfiguration();
		// Set connection timeout for initially establishing a connection.
		defaultClientConfiguration.setConnectionTimeout(parms.getInt(CONN_TIMEOUT_KEY));
		// Set socket timeout for data to be transferred.
		defaultClientConfiguration.setSocketTimeout(parms.getInt(CONN_TIMEOUT_KEY));
		// Set max connections.
		defaultClientConfiguration.setMaxConnections(parms.getInt(MAX_CONNECTIONS));
		// use expect continue HTTP/1.1 header.
		defaultClientConfiguration.withUseExpectContinue(false);

		// Set Signer type.
		defaultClientConfiguration.withSignerOverride(signType);

		if ((!parms.getStr(PROXY_HOST_KEY).equals("")) && (!parms.getStr(PROXY_PORT_KEY).equals(""))) {
			defaultClientConfiguration.setProxyHost(parms.getStr(PROXY_HOST_KEY));
			defaultClientConfiguration.setProxyPort(parms.getInt(PROXY_PORT_KEY));
		}

		return defaultClientConfiguration;
	}

	// 2021.7.13 Change SDK version: 1.10.x -> 1.12.x
	private AmazonS3 initClientV2() {
		logger.debug("initialize S3 client with storage config: {}", parms);

		ClientConfiguration clientConf = getDefaultClientConfiguration("S3SignerType");

		AWSCredentials myCredentials = new BasicAWSCredentials(accessKey, secretKey);

		EndpointConfiguration myEndpoint = new EndpointConfiguration(endpoint, "");

		client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(myCredentials))
				.withClientConfiguration(clientConf).withEndpointConfiguration(myEndpoint)
				.withPathStyleAccessEnabled(parms.getBoolean(PATH_STYLE_ACCESS_KEY)).build();

		logger.debug("S3 client has been initialized");

		return client;
	}

	private AmazonS3 initRestoreClient() {

		logger.debug("initialize S3 client with storage config: {}", parms);

		ClientConfiguration clientConf = getDefaultClientConfiguration("AWSS3V4SignerType");

		AWSCredentials myCredentials = new BasicAWSCredentials(accessKey, secretKey);

		EndpointConfiguration myendpoint = new EndpointConfiguration(endpoint, "");
		restoreClient = AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(myCredentials)).withClientConfiguration(clientConf)
				.withEndpointConfiguration(myendpoint)
				.withPathStyleAccessEnabled(parms.getBoolean(PATH_STYLE_ACCESS_KEY)).build();

		logger.debug("S3 client has been initialized");

		return restoreClient;
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

			if (isPrefetch) {
        		GetObjectRequest prefetchObjectRequest = new GetObjectRequest(container, object);
        		prefetchObjectRequest.putCustomRequestHeader("prefetch", "value");
        		S3Object s3Obj = client.getObject(prefetchObjectRequest);
        		stream = s3Obj.getObjectContent();
        	} else if (isRangeRequest) {
        		GetObjectRequest rangeObjectRequest = new GetObjectRequest(container, object);

        		Random rand = new Random();

        		long start = (long)(rand.nextDouble() * (fileLength - chunkLength));
        		long end = start + chunkLength - 1;

        		rangeObjectRequest.setRange(start, end);

        		S3Object s3Obj = client.getObject(rangeObjectRequest);
        		stream = s3Obj.getObjectContent();
        	} else {
        		S3Object s3Obj = client.getObject(container, object);
                stream = s3Obj.getObjectContent();
        	}
		} catch (AmazonServiceException ase) {
			throw new StorageException(ase);
		} catch (SdkClientException sce) {
			throw new StorageTimeoutException(sce);
		}
		return stream;
	}

	@Override
	public void restoreObject(String container, String object, Config config) {
		super.restoreObject(container, object, config);
		try {
			// Create and submit a request to restore an object from Glacier/Deep Archive
			// for some days.
			RestoreObjectRequest request = new RestoreObjectRequest(container, object, parms.getInt(RESTORE_DAYS_KEY));
			restoreClient.restoreObjectV2(request);

			ObjectMetadata response = restoreClient.getObjectMetadata(container, object);
			Boolean restoreFlag = response.getOngoingRestore();
			logger.info(object + " at bucket -> " + container + " | Restore days: " + parms.getInt(RESTORE_DAYS_KEY)
					+ ", and ongoing-request status is: " + restoreFlag);

		} catch (Exception e) {
			throw new StorageException(e);
		}
	}

	@Override
	public void createContainer(String container, Config config) {
		super.createContainer(container, config);
		try {
			container = container.split("/")[0];

			// 2021.7.13 Change SDK version: 1.10.x -> 1.12.x
			if (!client.doesBucketExistV2(container)) {
				client.createBucket(container);
			}
		} catch (Exception e) {
			throw new StorageException(e);
		}
	}

	@Override
	public void createObject(String container, String object, InputStream data, long length, Config config) {
		super.createObject(container, object, data, length, config);

		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(length);
		metadata.setContentType("application/octet-stream");

		// 2021.02.14
		// Set storage_class to other value if you need.
		String storageClass = parms.getStr(STORAGE_CLASS_KEY);
		if (storageClass != "STANDARD") {
			metadata.setHeader("x-amz-storage-class", storageClass);
		}

		// 2021.7.27, sine. another way to put object, and set Read limit to 5GiB+1.
		// https://github.com/awsdocs/aws-java-developer-guide/blob/master/doc_source/best-practices.rst
		PutObjectRequest request = new PutObjectRequest(container, object, data, metadata);
		
		request.getRequestClientOptions().setReadLimit((int)length + 1); // set limit to object length+1

		try {
			// client.putObject(container, object, data, metadata);

			client.putObject(request);

		} catch (AmazonServiceException ase) {
			throw new StorageException(ase);
		} catch (SdkClientException sce) {
			throw new StorageTimeoutException(sce);
		}
	}

	// 2021.8.3 updated, sine.
	@Override
	public void createMultipartObject(String container, String object, InputStream data, long length, Config config) {
		super.createMultipartObject(container, object, data, length, config);

		// Upload the file parts.
		long partSize = parms.getLong(PART_SIZE_KEY);

		// Set Metadata.
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(length);
		metadata.setContentType("application/octet-stream");

		String storageClass = parms.getStr(STORAGE_CLASS_KEY);
		if (storageClass != "STANDARD") {
			metadata.setHeader("x-amz-storage-class", storageClass);
		}

		// Create a list of ETag objects. You retrieve ETags for each object part
		// uploaded,
		// then, after each individual part has been uploaded, pass the list of ETags to
		// the request to complete the upload.
		List<PartETag> partETags = new ArrayList<PartETag>();

		try {
			// Initiate the multipart upload.
			InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(container, object,
					metadata);
			InitiateMultipartUploadResult initResponse = client.initiateMultipartUpload(initRequest);
			
			String uploadID = initResponse.getUploadId();

			long position = 0;

			for (int i = 1; position < length; i++) {
				// Because the last part could be less than 5 MB, adjust the part size as
				// needed.
				partSize = Math.min(partSize, (length - position));

				// Create the request to upload a part.
				UploadPartRequest uploadRequest = new UploadPartRequest()
						.withBucketName(container)
						.withKey(object)
						.withUploadId(uploadID)
						.withPartNumber(i)
						.withInputStream(data)
						.withPartSize(partSize);

				uploadRequest.getRequestClientOptions().setReadLimit((int)length+1); // length+1

				// Upload the part and add the response's ETag to our list.
				UploadPartResult uploadResult = client.uploadPart(uploadRequest);
				partETags.add(uploadResult.getPartETag());

				position += partSize;
			}

			// Complete the multipart upload.
			CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest(container, object,
					uploadID, partETags);
			client.completeMultipartUpload(compRequest);

		} catch (AmazonServiceException ase) {
			throw new StorageException(ase);
		} catch (SdkClientException sce) {
			throw new StorageTimeoutException(sce);
		}
	}

	@Override
	public void deleteContainer(String container, Config config) {
		super.deleteContainer(container, config);
		try {
			container = container.split("/")[0];
			// 2021.07.13, sine: SDK version 1.10.x -> 1.12.x
			if (client.doesBucketExistV2(container)) {
				client.deleteBucket(container);
			}
		} catch (Exception e) {
			throw new StorageException(e);
		}
	}

	@Override
	public void deleteObject(String container, String object, Config config) {
		super.deleteObject(container, object, config);
		try {
			client.deleteObject(container, object);
		} catch (Exception e) {
			throw new StorageException(e);
		}
	}

	@Override
	public InputStream getList(String container, String object, Config config) {
		super.getList(container, object, config);
		InputStream stream = null;
		try {
			// 2021.7.14 Change SDK version: 1.10.x -> 1.12.x
			ListObjectsV2Result result = client.listObjectsV2(container, object);

			stream = new ByteArrayInputStream(result.getObjectSummaries().toString().getBytes());
		} catch (Exception e) {
			throw new StorageException(e);
		}

		return stream;
	}
}
