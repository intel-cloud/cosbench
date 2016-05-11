package com.intel.cosbench.api.openio;

import static com.intel.cosbench.client.openio.OioStorageConstants.ACCOUNT_KEY;
import static com.intel.cosbench.client.openio.OioStorageConstants.NS_KEY;
import static com.intel.cosbench.client.openio.OioStorageConstants.PROXYD_URL_KEY;
import static io.openio.sds.models.OioUrl.url;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.intel.cosbench.api.storage.NoneStorage;
import com.intel.cosbench.api.storage.StorageException;
import com.intel.cosbench.config.Config;
import com.intel.cosbench.log.Logger;

import io.openio.sds.Client;
import io.openio.sds.ClientBuilder;
import io.openio.sds.exceptions.ContainerExistException;
import io.openio.sds.exceptions.ContainerNotEmptyException;
import io.openio.sds.exceptions.SdsException;
import io.openio.sds.models.ListOptions;
import io.openio.sds.models.ObjectInfo;
import io.openio.sds.models.ObjectList;
import io.openio.sds.models.ObjectList.ObjectView;

/**
 * 
 * @author Christopher Dedeurwaerder
 *
 */
public class OpenIOStorage extends NoneStorage {

    private Client client;
    private String account;
    private Pattern errCodePattern = Pattern.compile(".*(\\p{Digit}{3}).*");

    @Override
    public void init(Config config, Logger logger) {
        super.init(config, logger);
        String ns = config.get(NS_KEY);
        String proxydurl = config.get(PROXYD_URL_KEY);
        client = ClientBuilder
                .newClient(ns, proxydurl);
        account = config.get(ACCOUNT_KEY);
        parms.put(NS_KEY, ns);
        parms.put(PROXYD_URL_KEY, proxydurl);
        parms.put(ACCOUNT_KEY, account);
    }

    /**
     * Hack the exception message to put the error code where COSBench expects
     * it.
     *
     * @param wrapped
     *            The exception to wrap
     * @return a new StorageException with the {@link SdsException} as cause
     */
    private StorageException makeStorageException(SdsException wrapped) {
        String origMsg = wrapped.getMessage();
        if (wrapped instanceof ContainerNotEmptyException
                || wrapped instanceof ContainerExistException)
            return new StorageException("HTTP/1.1 409 " + origMsg, wrapped);

        Matcher codeMatcher = errCodePattern.matcher(origMsg);
        if (codeMatcher.find()) {
            return new StorageException("HTTP/1.1 " + codeMatcher.group(1)
                    + " " + origMsg, wrapped);
        }
        return new StorageException("HTTP/1.1 500 " + origMsg, wrapped);
    }

    @Override
    public InputStream getObject(String container, String object,
            Config config) {
        try {
            ObjectInfo oinf = client
                    .getObjectInfo(url(account, container, object));
            return client.downloadObject(oinf);
        } catch (SdsException e) {
            throw makeStorageException(e);
        }
    }

    @Override
    public InputStream getList(String container, String object, Config config) {
        StringBuilder sb = new StringBuilder();
        try {
            // TODO: loop on listContainer until the answer is empty
            ObjectList objects = client.listContainer(url(account, container),
                    new ListOptions());
            for (ObjectView obj : objects.objects()) {
                sb.append(obj.name()).append('\n');
            }
        } catch (SdsException e) {
            throw makeStorageException(e);
        }
        return new ByteArrayInputStream(sb.toString().getBytes());
    }

    @Override
    public void createContainer(String container, Config config) {
        super.createContainer(container, config);
        try {
            client.createContainer(url(account, container));
        } catch (ContainerExistException e) {
            this.logger.warn("Container " + container + " already exists!", e);
        } catch (SdsException e) {
            throw makeStorageException(e);
        }
    }

    @Override
    public void createObject(String container, String object, InputStream data,
            long length, Config config) {
        super.createObject(container, object, data, length, config);
        try {
            client.putObject(url(account, container, object),
                    length, data);
        } catch (SdsException e) {
            throw makeStorageException(e);
        }
    }

    @Override
    public void deleteContainer(String container, Config config) {
        super.deleteContainer(container, config);
        try {
            client.deleteContainer(url(account, container));
        } catch (SdsException e) {
            throw makeStorageException(e);
        }
    }

    @Override
    public void deleteObject(String container, String object, Config config) {
        super.deleteObject(container, object, config);
        try {
            client.deleteObject(url(account, container, object));
        } catch (SdsException e) {
            throw makeStorageException(e);
        }
    }
}
