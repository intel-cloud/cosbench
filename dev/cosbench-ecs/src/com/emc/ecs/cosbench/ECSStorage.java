/**
 * Copyright 2014-2017 EMC Corporation. All Rights Reserved.
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

package com.emc.ecs.cosbench;

import com.emc.object.Protocol;
import com.emc.object.Range;
import com.emc.object.s3.S3Client;
import com.emc.object.s3.S3Config;
import com.emc.object.s3.S3ObjectMetadata;
import com.emc.object.s3.bean.MetadataSearchDatatype;
import com.emc.object.s3.bean.MetadataSearchKey;
import com.emc.object.s3.jersey.S3JerseyClient;
import com.emc.object.s3.request.CreateBucketRequest;
import com.emc.object.s3.request.PutObjectRequest;
import com.emc.object.util.ConfigUri;
import com.intel.cosbench.api.context.AuthContext;
import com.intel.cosbench.api.storage.NoneStorage;
import com.intel.cosbench.config.Config;
import com.intel.cosbench.log.Logger;
import com.sun.jersey.client.urlconnection.URLConnectionClientHandler;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import java.util.Random;
import java.util.TimeZone;

/**
 * COSBench Storage API for EMC ECS Object Storage interface.
 * Enables use of ECS smart client for load balancing.
 */
public class ECSStorage extends NoneStorage
        implements ECSConstants {

    /* (non-Javadoc)
     * @see com.intel.cosbench.api.storage.NoneStorage#abort()
     */
    @Override
    public void abort() {
        // TODO Auto-generated method stub
        super.abort();
    }

    public static void main(String[] args) {
        ECSStorage storage = new ECSStorage();

        TestConfig config = new TestConfig();
        config.addMapping(METADATA_NAMES_KEY, "string,integer,double,date");
        List<String> metadataNames = getMetadataNames(config);
        if (metadataNames.size() != 4) {
            System.err.println("Wrong number of metadata names: 4 != " + metadataNames.size());
        }

        config.addMapping("string_type", "string");
        config.addMapping("date_type", "date");
        config.addMapping("double_type", "double");
        config.addMapping("int_type", "int");

        String type = getType("string", config);
        if (!METADATA_TYPE_STRING.equals(type)) {
            System.err.println("Wrong type: " + METADATA_TYPE_STRING + " != " + type);
        }

        type = getType("date", config);
        if (!METADATA_TYPE_DATE.equals(type)) {
            System.err.println("Wrong type: " + METADATA_TYPE_DATE + " != " + type);
        }

        type = getType("int", config);
        if (!METADATA_TYPE_INT.equals(type)) {
            System.err.println("Wrong type: " + METADATA_TYPE_INT + " != " + type);
        }

        type = getType("double", config);
        if (!METADATA_TYPE_DOUBLE.equals(type)) {
            System.err.println("Wrong type: " + METADATA_TYPE_DOUBLE + " != " + type);
        }

        config.addMapping("int_maximum", "10");
        config.addMapping("int_minimum", "5");

        IStringGenerator ofInt = storage.createRandomIntGenerator("int", null, "int_minimum", "int_maximum", config);
        for (int i = 0; i < 100; ++i) {
            int nextInt = Integer.parseInt(ofInt.nextString());
            if ((nextInt < 5) || (nextInt >= 10)) {
                System.err.println(">>>> Bad int " + nextInt);
            } else {
                System.out.println(i + ": " + nextInt);
            }
        }

//        ofInt = storage.createRandomIntGenerator("int2", null, "int2_minimum", "int2_maximum", config);
//        for (int i = 0; i < 100; ++i) {
//            System.out.println(i + ": " + ofInt.nextString());
//        }

        config.addMapping("double_minimum", "-5.07");
        config.addMapping("double_maximum", "15.005");

        IStringGenerator ofDouble = storage.createRandomDoubleGenerator("double", null, "double_minimum", "double_maximum", config);
        for (int i = 0; i < 100; ++i) {
            double nextDouble = Double.parseDouble(ofDouble.nextString());
            if ((nextDouble < -5.07) || (nextDouble >= 15.005)) {
                System.err.println(">>>> Bad double " + nextDouble);
            } else {
                System.out.println(i + ": " + nextDouble);
            }
        }

        ofDouble = storage.createRandomDoubleGenerator("double2", null, "double2_minimum", "double2_maximum", config);
        for (int i = 0; i < 100; ++i) {
            System.out.println(i + ": " + ofDouble.nextString());
        }

        config.addMapping("string_maximum", "10");
        config.addMapping("string_minimum", "0");
        config.addMapping("string_characters", "0123456789aZ%");

        IStringGenerator generator = storage.createRandomStringGenerator("string", null, "string_minimum", "string_maximum", config);
        for (int i = 0; i < 100; ++i) {
            String nextString = generator.nextString();
            if ((nextString.length() < 0) || (nextString.length() >= 10)) {
                System.err.println(">>>> Bad string " + nextString);
            } else {
                boolean badString = false;
                for (int j = 0; j < nextString.length(); ++j) {
                    char nextChar = nextString.charAt(j);
                    if ((nextChar < '0' || nextChar > '9') && (nextChar != 'a' && nextChar != 'Z' && nextChar != '%')) {
                        badString = true;
                    }
                }
                if (badString) {
                    System.err.println(">>>> Bad string character " + nextString);
                } else {
                    System.out.println(i + ": " + nextString);
                }
            }
        }

        generator = storage.createRandomStringGenerator("string2", null, "string2_minimum", "string2_maximum", config);
        for (int i = 0; i < 100; ++i) {
            String nextString = generator.nextString();
            boolean badString = false;
            for (int j = 0; j < nextString.length(); ++j) {
                char nextChar = nextString.charAt(j);
                if (!((nextChar >= '0' && nextChar <= '9') || (nextChar >= 'a' && nextChar <= 'z') || (nextChar >= 'A' && nextChar <= 'Z'))) {
                    badString = true;
                }
            }
            if (badString) {
                System.err.println(">>>> Bad string character " + nextString);
            } else {
                System.out.println(i + ": " + nextString);
            }
        }

        String firstDate = "2015-01-30 13.01.02";
        String lastDate = "2017-08-20 13.01.59";
        String dateFormat = "yyyy-MM-dd HH.mm.ss";
        config.addMapping("date_maximum", lastDate);
        config.addMapping("date_minimum", firstDate);
        config.addMapping("date_format", dateFormat);

        IStringGenerator dateGenerator = storage.createRandomDateGenerator("date", null, "date_minimum", "date_maximum", config);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        for (int i = 0; i < 100; ++i) {
            String nextString = dateGenerator.nextString();
            if ((nextString.compareTo(firstDate) < 0) || (nextString.compareTo(lastDate)> 0)) {
                System.err.println(">>>> Bad date " + nextString);
            } else {
                try {
                    simpleDateFormat.parse(nextString);
                    System.out.println(i + ": " + nextString);
                } catch (Exception e) {
                    System.err.println(">>>> Bad date format " + nextString);
                }
            }
        }

        dateGenerator = storage.createRandomDateGenerator("date2", null, "date2_minimum", "date2_maximum", config);
        simpleDateFormat = new SimpleDateFormat(METADATA_DEFAULT_FORMAT_DATE);
        for (int i = 0; i < 100; ++i) {
            String nextString = dateGenerator.nextString();
            try {
                simpleDateFormat.parse(nextString);
                System.out.println(i + ": " + nextString);
            } catch (Exception e) {
                System.err.println(">>>> Bad date format " + nextString);
            }
        }

    }

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

    private Random random = new Random();
    private final Map<String, IStringGenerator> metadataGenerators = new HashMap<String, IStringGenerator>();

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

        String configUriString = config.get(CONFIG_URI_KEY, "");
        if ((configUriString != null) && !configUriString.trim().isEmpty()) {
            ConfigUri<S3Config> s3Uri = new ConfigUri<S3Config>(S3Config.class);
            s3CliConfig = s3Uri.parseUri(configUriString);
            parms.put(CONFIG_URI_KEY, configUriString);
        } else {
            endpoint = config.get(ENDPOINT_KEY, ENDPOINT_DEFAULT);
            accessKey = config.get(AUTH_USERNAME_KEY, AUTH_USERNAME_DEFAULT);
            secretKey = config.get(AUTH_PASSWORD_KEY, AUTH_PASSWORD_DEFAULT);
            boolean pathStyleAccess = config.getBoolean(PATH_STYLE_ACCESS_KEY, PATH_STYLE_ACCESS_DEFAULT);
//        String proxyHost = config.get(PROXY_HOST_KEY, "");
//        String proxyPort = config.get(PROXY_PORT_KEY, "");
            String namespace = config.get(NAMESPACE_KEY, "");
            smartClient = config.getBoolean(SMART_CLIENT_KEY, SMART_CLIENT_DEFAULT);

            //Put new info. to environment variables where applicable
            parms.put(ENDPOINT_KEY, endpoint);
            parms.put(AUTH_USERNAME_KEY, accessKey);
            parms.put(AUTH_PASSWORD_KEY, secretKey);
            parms.put(PATH_STYLE_ACCESS_KEY, pathStyleAccess);
//            parms.put(PROXY_HOST_KEY, proxyHost);
//            parms.put(PROXY_PORT_KEY, proxyPort);
            parms.put(NAMESPACE_KEY, namespace);
            parms.put(SMART_CLIENT_KEY, smartClient);

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
            if (!namespace.equals("")) s3CliConfig.setNamespace(namespace);
        }

        try {
            System.setProperty("http.maxConnections", Integer.toString(Thread.activeCount()) + 128);
            if(!connTimeout.equals("")) System.setProperty("com.sun.jersey.client.property.readTimeout", connTimeout);
            if(!readTimeout.equals("")) System.setProperty("com.sun.jersey.client.property.connectTimeout", readTimeout);
        }
        catch (Exception e) {
            logger.error(e.getMessage());
            //throw new StorageException(e);
        }

        httpClient = config.get(HTTP_CLIENT_KEY, HTTP_CLIENT_DEFAULT);
        parms.put(HTTP_CLIENT_KEY, httpClient);
        logger.debug("using storage config: {}", parms);

        if ("java".equals(httpClient)) {
            s3Client =  new S3JerseyClient(s3CliConfig, new URLConnectionClientHandler());
        }
        else{
            s3Client = new S3JerseyClient(s3CliConfig);
        }

        random = new Random();
        List<String> metadataNames = getMetadataNames(config);
        for (String name : metadataNames) {
            createMetadataGenerator(name, config);
        }
        logger.debug("Created metadata generators: " + metadataGenerators.size());
    }

    /**
     * @param name
     * @param config
     * @return
     */
    private IStringGenerator createMetadataGenerator(String name, Config config) {
        IStringGenerator metadataGenerator = null;
        String type = getType(name, config);
        String minimumKey = getMinimumKey(name);
        String maximumKey = getMaximumKey(name);
        String[] valuesArray = getValuesArray(config, name);
        if (METADATA_TYPE_INT.equals(type)) {
            metadataGenerator = createRandomIntGenerator(name, valuesArray, minimumKey, maximumKey, config);
        } else if (METADATA_TYPE_DOUBLE.equals(type)) {
            metadataGenerator = createRandomDoubleGenerator(name, valuesArray, minimumKey, maximumKey, config);
        } else if (METADATA_TYPE_STRING.equals(type)) {
            metadataGenerator = createRandomStringGenerator(name, valuesArray, minimumKey, maximumKey, config);
        } else if (METADATA_TYPE_DATE.equals(type)) {
            metadataGenerator = createRandomDateGenerator(name, valuesArray, minimumKey, maximumKey, config);
        }
        if (metadataGenerator != null) {
            metadataGenerators.put(name, metadataGenerator);
        }
        return metadataGenerator;
    }

    /**
     * @param config
     * @param name
     * @return
     */
    private String[] getValuesArray(Config config, String name) {
        ArrayList<String> arrayList = getArrayList(config, name + METADATA_VALUES_ARRAY_SUFFIX);
        return arrayList.isEmpty() ? null : arrayList.toArray(new String[arrayList.size()]);
    }

    /**
     * @param config
     * @return
     */
    private static List<String> getMetadataNames(Config config) {
        return getArrayList(config, METADATA_NAMES_KEY);
    }

    /**
     * @param config
     * @param key
     * @return
     */
    private static ArrayList<String> getArrayList(Config config, String key) {
        ArrayList<String> values = new ArrayList<String>();
        if (config != null) {
            for (String value : config.get(key, "").split(",")) {
                value = value.trim();
                if (!value.isEmpty()) {
                    values.add(value);
                }
            }
        }
        return values;
    }

    /**
     * @param name
     * @param config
     * @return
     */
    private static String getType(String name, Config config) {
        return config.get(name + METADATA_TYPE_KEY_SUFFIX, "");
    }

    /**
     * @param name
     * @return
     */
    private static String getMinimumKey(String name) {
        return name + METADATA_MINIMUM_KEY_SUFFIX;
    }

    /**
     * @param name
     * @return
     */
    private static String getMaximumKey(String name) {
        return name + METADATA_MAXIMUM_KEY_SUFFIX;
    }

    /**
     * @param name
     * @param valuesArray 
     * @param minimumKey
     * @param maximumKey
     * @param config
     * @return
     */
    private IStringGenerator createRandomIntGenerator(String name, String[] valuesArray, String minimumKey, String maximumKey, Config config) {
        int minimum = config.getInt(minimumKey, Integer.MIN_VALUE);
        int maximum = config.getInt(maximumKey, Integer.MAX_VALUE);
        return new RandomIntGenerator(random, valuesArray, minimum, maximum);
    }

    /**
     * @param name
     * @param valuesArray 
     * @param minimumKey
     * @param maximumKey
     * @param config
     * @return
     */
    private IStringGenerator createRandomDoubleGenerator(String name, String[] valuesArray, String minimumKey, String maximumKey, Config config) {
        double minimum = config.getDouble(minimumKey, 0);
        double maximum = config.getDouble(maximumKey, Double.MAX_VALUE);
        return new RandomDoubleGenerator(random, valuesArray, minimum, maximum);
    }

    /**
     * @param name
     * @param valuesArray 
     * @param minimumKey
     * @param maximumKey
     * @param config
     * @return
     */
    private IStringGenerator createRandomStringGenerator(String name, String[] valuesArray, String minimumKey, String maximumKey, Config config) {
        int minimum = config.getInt(minimumKey, 0);
        int maximum = config.getInt(maximumKey, 1024);
        return new RandomStringGenerator(random, valuesArray, minimum, maximum, config.get(name + METADATA_CHARACTERS_KEY_SUFFIX, ""));
    }

    /**
     * @param name
     * @param valuesArray 
     * @param minimumKey
     * @param maximumKey
     * @param config
     * @return
     */
    private IStringGenerator createRandomDateGenerator(String name, String[] valuesArray, String minimumKey, String maximumKey, Config config) {
        String format = config.get(name + METADATA_FORMAT_KEY_SUFFIX, METADATA_DEFAULT_FORMAT_DATE);
        long minimum = 0;
        long maximum = Long.MAX_VALUE;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(METADATA_TIME_ZONE));
        String startDate = config.get(minimumKey, "");
        String endDate = config.get(maximumKey, "");
        if (!"".equals(startDate)) {
            try {
                minimum = simpleDateFormat.parse(startDate).getTime();
            } catch (ParseException e) {
                logger.error("Unparseable start date: " + startDate);
            }
        }
        if (!"".equals(endDate)) {
            try {
                maximum = simpleDateFormat.parse(endDate).getTime();
            } catch (ParseException e) {
                logger.error("Unparseable end date: " + endDate);
            }
        }
        return new RandomDateGenerator(simpleDateFormat, random, valuesArray, minimum, maximum);
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
        logger.info(("Releasing clients"));
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
            if (s3Client.bucketExists(container)) {
                logger.info((new StringBuilder("Bucket exists: ")).append(container).toString());
            } else {
                logger.info((new StringBuilder("Creating ")).append(container).toString());
                List<MetadataSearchKey> metadataSearchKeys = new ArrayList<MetadataSearchKey>();
                for (String name : getMetadataNames(config)) {
                    IStringGenerator metadataGenerator = metadataGenerators.get(name);
                    if (metadataGenerator == null) {
                        metadataGenerator = createMetadataGenerator(name, config);
                    }
                    if (metadataGenerator != null) {
                        MetadataSearchDatatype metadataSearchDatatype = null;
                        if (metadataGenerator instanceof RandomIntGenerator) {
                            metadataSearchDatatype = MetadataSearchDatatype.integer;
                        } else if (metadataGenerator instanceof RandomDoubleGenerator) {
                            metadataSearchDatatype = MetadataSearchDatatype.decimal;
                        } else if (metadataGenerator instanceof RandomStringGenerator) {
                            metadataSearchDatatype = MetadataSearchDatatype.string;
                        } else if (metadataGenerator instanceof RandomDateGenerator) {
                            metadataSearchDatatype = MetadataSearchDatatype.datetime;
                        }
                        metadataSearchKeys.add(new MetadataSearchKey(METADATA_NAME_START + name, metadataSearchDatatype));
                    }
                }
                CreateBucketRequest createBucketRequest = new CreateBucketRequest(container);
                if (!metadataSearchKeys.isEmpty()) {
                    createBucketRequest.setMetadataSearchKeys(metadataSearchKeys);
                }
                s3Client.createBucket(createBucketRequest);
            }
        } catch (Exception e) {
            logger.info("Creation error: " + container);
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
            S3ObjectMetadata s3ObjectMetadata = new S3ObjectMetadata().withContentLength(length);
            Map<String, String> randomMetadata = generateRandomMetadata(config);
            for (Entry<String, String> entry : randomMetadata.entrySet()) {
                s3ObjectMetadata.addUserMetadata(entry.getKey(), entry.getValue());
            }
            PutObjectRequest req = new PutObjectRequest(container, object, data).withObjectMetadata(s3ObjectMetadata);
            s3Client.putObject(req);
        } catch (Exception e) {
            logger.info("Creation error: " + object);
            logger.error(e.getMessage());
            //throw new StorageException(e);
        }
    }

    /**
     * @param config
     * @return
     */
    private Map<String, String> generateRandomMetadata(Config config) {
        Map<String, String> randomMetada = new HashMap<String, String>();
        for (String name : getMetadataNames(config)) {
            IStringGenerator metadataGenerator = metadataGenerators.get(name);
            if (metadataGenerator != null) {
                randomMetada.put(name, metadataGenerator.nextString());
            }
        }
        return randomMetada;
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

    private static class TestConfig implements Config {

        Map<String, String> _map = new HashMap<String, String>();

        public void addMapping(String key, String value) {
            _map.put(key, value);
        }

        @Override
        public String get(String key) {
            return get(key, null);
        }

        @Override
        public String get(String key, String value) {
            String bareString = _map.get(key);
            return (bareString != null) ? bareString : value;
        }

        @Override
        public int getInt(String key) {
            return getInt(key, 0);
        }

        @Override
        public int getInt(String key, int value) {
            String number = get(key);
            if ((number != null) && (!"".equals(number.trim()))) {
                try {
                    return Integer.parseInt(number);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
            return value;
        }

        @Override
        public long getLong(String key) {
            return getLong(key, 0);
        }

        @Override
        public long getLong(String key, long value) {
            String number = get(key);
            if ((number != null) && (!"".equals(number.trim()))) {
                try {
                    return Long.parseLong(number);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
            return value;
        }

        @Override
        public double getDouble(String key) {
            return getDouble(key, 0);
        }

        @Override
        public double getDouble(String key, double value) {
            String number = get(key);
            if ((number != null) && (!"".equals(number.trim()))) {
                try {
                    return Double.parseDouble(number);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
            return value;
        }

        @Override
        public boolean getBoolean(String key) {
            return getBoolean(key, false);
        }

        @Override
        public boolean getBoolean(String key, boolean value) {
            String booleanString = get(key);
            if ((booleanString != null) && (!"".equals(booleanString.trim()))) {
                try {
                    return Boolean.parseBoolean(booleanString);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
            return value;
        }

    }

}