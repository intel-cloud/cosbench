package com.intel.cosbench.api.GcsStor;

import static com.intel.cosbench.client.GcsStor.GcsStorConstants.CONN_TIMEOUT_KEY;
import static com.intel.cosbench.client.GcsStor.GcsStorConstants.CONN_TIMEOUT_DEFAULT;
import static com.intel.cosbench.client.GcsStor.GcsStorConstants.JSON_KEY_FILE;
import static com.intel.cosbench.client.GcsStor.GcsStorConstants.JSON_KEY_FILE_DEFAULT;
import static com.intel.cosbench.client.GcsStor.GcsStorConstants.PROJECT_ID;
import static com.intel.cosbench.client.GcsStor.GcsStorConstants.PROJECT_ID_DEFAULT;

import java.io.*;

import com.intel.cosbench.api.storage.*;
import com.intel.cosbench.api.context.*;
import com.intel.cosbench.config.Config;
import com.intel.cosbench.log.Logger;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.StorageScopes;
import com.google.api.services.storage.model.Bucket;
import com.google.api.services.storage.model.StorageObject;

public class GcsStorage extends NoneStorage {
	private int timeout;
	private String jsonKeyFile;
	private String projectId;
	private static Storage client;

	private static final JsonFactory JSON_FACTORY = JacksonFactory
			.getDefaultInstance();

	@Override
	public void init(Config config, Logger logger) {
		super.init(config, logger);
		initParms(config);
		try {
			InputStream input = new FileInputStream(jsonKeyFile);
			GoogleCredential credential = GoogleCredential.fromStream(input);
			if (credential.createScopedRequired()) {
				credential = credential.createScoped(StorageScopes.all());
			}
			HttpTransport httpTransport = GoogleNetHttpTransport
					.newTrustedTransport();
			client = new Storage.Builder(httpTransport, JSON_FACTORY,
					credential).build();
			logger.debug("GCS client has been initialized");
		} catch (Exception e) {
			throw new StorageException(e);
		}
	}

	private void initParms(Config config) {
		timeout = config.getInt(CONN_TIMEOUT_KEY, CONN_TIMEOUT_DEFAULT);
		parms.put(CONN_TIMEOUT_KEY, timeout);

		jsonKeyFile = config.get(JSON_KEY_FILE, JSON_KEY_FILE_DEFAULT);
		parms.put(JSON_KEY_FILE, jsonKeyFile);

		projectId = config.get(PROJECT_ID, PROJECT_ID_DEFAULT);
		parms.put(PROJECT_ID, projectId);

		logger.debug("using storage config: {}", parms);
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
		InputStream stream;
		try {
			Storage.Objects.Get getRequest = client.objects().get(container,
					object);
			stream = getRequest.executeMediaAsInputStream();
		} catch (IOException e) {
			throw new StorageException(e);
		}
		return stream;
	}

	@Override
	public void createContainer(String container, Config config) {
		super.createContainer(container, config);
		Bucket newBucket = new Bucket();
		newBucket.setName(container);
		try {
			Storage.Buckets.Insert bucketInsertRequest = client.buckets()
					.insert(projectId, newBucket);
			bucketInsertRequest.execute();
		} catch (IOException e) {
			throw new StorageException(e);
		}
	}

	@Override
	public void createObject(String container, String object, InputStream data,
			long length, Config config) {
		super.createObject(container, object, data, length, config);
		InputStreamContent contentStream = new InputStreamContent(
				"application/octet-stream", data);
		StorageObject objectMetadata = new StorageObject().setName(object);
		try {
			Storage.Objects.Insert insertRequest = client.objects().insert(
					container, objectMetadata, contentStream);
			insertRequest.execute();
		} catch (IOException e) {
			throw new StorageException(e);
		}

	}

	@Override
	public void deleteContainer(String container, Config config) {
		super.deleteContainer(container, config);
		try {
			Storage.Buckets.Delete bucketDeleteRequest = client.buckets()
					.delete(container);
			bucketDeleteRequest.execute();
		} catch (IOException e) {
			throw new StorageException(e);
		}
	}

	@Override
	public void deleteObject(String container, String object, Config config) {
		super.deleteObject(container, object, config);
		try {
			client.objects().delete(container, object).execute();
		} catch (Exception e) {
			throw new StorageException(e);
		}
	}

}
