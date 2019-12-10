package com.intel.cosbench.api.AzureBlobStor;

import java.io.*;
import java.util.Collections;
import java.time.Duration;

import static com.intel.cosbench.client.AzureBlobStor.AzureBlobConstants;

import com.intel.cosbench.api.context.AuthContext;
import com.intel.cosbench.api.storage.*;
import com.intel.cosbench.config.Config;
import com.intel.cosbench.log.Logger;

import com.azure.core.util.Context;
import com.azure.storage.blob.models.*;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobClient;
import com.azure.core.http.rest.Response;
import com.azure.storage.common.StorageSharedKeyCredential;

public class AzureBlobStorage extends NoneStorage {
    private long timeout;

    private String endpoint;
    private String accountName;
    private String accountKey;

    private StorageSharedKeyCredential credentials;
    private BlobServiceClient client;

    @Override
    public void init(Config config, Logger logger) {
        super.init(config, logger);

        timeout = config.getLong(CONN_TIMEOUT_KEY, CONN_TIMEOUT_DEFAULT);
        parms.put(CONN_TIMEOUT_KEY, timeout);

        accountName = config.get(ACCOUNT_NAME_KEY, ACCOUNT_NAME_DEFAULT);
        parms.put(ACCOUNT_NAME_KEY, accountName);

        accountKey = config.get(ACCOUNT_KEY_KEY, ACCOUNT_KEY_DEFAULT);
        parms.put(ACCOUNT_KEY_KEY, accountKey);

        endpoint = config.get(ENDPOINT_KEY, ENDPOINT_DEFAULT);
        parms.put(ENDPOINT_KEY, endpoint);

        credentials = new StorageSharedKeyCredential(accountName, accountKey);

        client = new BlobServiceClientBuilder()
            .endpoint(endpoint)
            .credential(credentials)
            .buildClient();

        logger.debug("azure adapter config: {}", parms);
    }

    @Override
    public void abort() {
        super.abort();
    }

    @Override
    public void dispose() {
        super.dispose();

        client = null;
        credentials = null;
    }

    @Override
    public void setAuthContext(AuthContext info) {
        super.setAuthContext(info);
    }

    private BlobContainerClient createContainerClient(String  container) {
        return client.getBlobContainerClient(container);
    }

    private BlobClient createBlobClient(String container, String object) {
        return createContainerClient(container)
            .getBlobClient(object);
    }

    @Override
    public void createContainer(String container, Config config) {
        super.createContainer(container, config);
        try {
            createContainerClient(container)
                .createWithResponse(
                    Collections.<String, String>emptyMap(),
                    null,
                    Duration.ofMillis(timeout),
                    Context.NONE
                );
        } catch (BlobStorageException bse) {
            throw new StorageException(bse.getMessage(), bse);
        } catch (RuntimeException rte) {
            throw new StorageTimeoutException(rte);
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public void deleteContainer(String container, Config config) {
        super.deleteContainer(container, config);
        try {
            createContainerClient(container)
                .deleteWithResponse(
                    null,
                    Duration.ofMillis(timeout),
                    Context.NONE
                );
        } catch (BlobStorageException bse) {
            throw new StorageException(bse.getMessage(), bse);
        } catch (RuntimeException rte) {
            throw new StorageTimeoutException(rte);
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public InputStream getObject(String container, String object, Config config) {
        super.getObject(container, object, config);

        InputStream stream;
        try {
            stream = createBlobClient(container, object).openInputStream();
        } catch (BlobStorageException bse) {
            throw new StorageException(bse.getMessage(), bse);
        } catch (RuntimeException rte) {
            throw new StorageTimeoutException(rte);
        } catch (Exception e) {
            throw new StorageException(e);
        }
        return stream;
    }

    @Override
    public void createObject(
        String container, String object, InputStream data, long length, Config config
    ) {
        super.createObject(container, object, data, length, config);

        try {
            createBlobClient(container, object)
                .getBlockBlobClient()
                .uploadWithResponse(
                    data,
                    length,
                    null,
                    Collections.<String, String>emptyMap(),
                    AccessTier.HOT,
                    null,
                    null,
                    Duration.ofMillis(timeout),
                    Context.NONE
                );
        } catch (BlobStorageException bse) {
            throw new StorageException(bse.getMessage(), bse);
        } catch (RuntimeException rte) {
            throw new StorageTimeoutException(rte);
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public void deleteObject(String container, String object, Config config) {
        super.deleteObject(container, object, config);

        try {
            createBlobClient(container, object)
                .deleteWithResponse(
                    DeleteSnapshotsOptionType.INCLUDE,
                    null,
                    Duration.ofMillis(timeout),
                    Context.NONE
                );
        } catch (BlobStorageException bse) {
            throw new StorageException(bse.getMessage(), bse);
        } catch (RuntimeException rte) {
            throw new StorageTimeoutException(rte);
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }
}

