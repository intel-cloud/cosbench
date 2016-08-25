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

//Cloudian
import java.util.List;
import java.util.ArrayList;
import org.apache.commons.collections.iterators.LoopingListIterator;
import com.amazonaws.DnsResolver;
import com.amazonaws.SystemDefaultDnsResolver;
import java.net.InetAddress;
//Cloudian

public class S3Storage extends NoneStorage {
	private int timeout;
	
    private String accessKey;
    private String secretKey;
    private String endpoint;
    
    //Cloudian
    private static final String TOPOLOGY_CONF_FILE = "cloudian-topology.properties";
    private static volatile LoopingListIterator s3Hosts;
    //Cloudian
    
    private AmazonS3 client;

    //Cloudian
    static {
    	final File f = new File(TOPOLOGY_CONF_FILE);
    	if (f.exists()) {
    		List<String> loadedList = new ArrayList<String>();
    		BufferedReader br;
    		try {
    			br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(f)));
    			String line = br.readLine();
    			while (line != null) {
    				if (!line.isEmpty()) {
    					loadedList.add(line);
    				}
    				line = br.readLine();
    			}
    			br.close();
    		} catch (FileNotFoundException e) {
    			e.printStackTrace();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}

    		//Don't need strict consistency to this
    		s3Hosts = new LoopingListIterator(loadedList);
    	} else {
    		s3Hosts = null;
    	}
    }
    
    class fixedDns implements DnsResolver {
        String ip = null;
        DnsResolver other;
        public fixedDns(final String thatIp) {
                this(new SystemDefaultDnsResolver( ), thatIp);
        }
        public fixedDns(final DnsResolver thatDnsResolver, final String thatIp) {
            this.other = thatDnsResolver;
            this.ip = thatIp;
        }
        public InetAddress[] resolve(final String host)
            throws java.net.UnknownHostException {
        	if (this.other != null && this.ip != null) {
        		InetAddress[] iaddrs = other.resolve(this.ip);
        			return iaddrs;
            }
            return resolve(host);
        }
    }
    //Cloudian

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
        clientConf.withUseExpectContinue(false);
        clientConf.withSignerOverride("S3SignerType");
//        clientConf.setProtocol(Protocol.HTTP);
		if((!proxyHost.equals(""))&&(!proxyPort.equals(""))){
			clientConf.setProxyHost(proxyHost);
			clientConf.setProxyPort(Integer.parseInt(proxyPort));
		}
        
		//Cloudian
		if (s3Hosts != null
				&& s3Hosts.hasNext()) {
			String thisHostSpec = (String) s3Hosts.next();
	        int last_colon = thisHostSpec.lastIndexOf(':');
	        String s3Host;
	        int s3Port;
	        if (last_colon > -1) {
	        	s3Host = thisHostSpec.substring(0, last_colon);
	            s3Port = Integer.parseInt(thisHostSpec.substring(last_colon + 1));
	        } else {
	        	s3Host = thisHostSpec;
	            s3Port = 80;
	        }    
	        clientConf.setDnsResolver(new fixedDns(s3Host));
		}
		//Cloudian
		
        AWSCredentials myCredentials = new BasicAWSCredentials(accessKey, secretKey);
        client = new AmazonS3Client(myCredentials, clientConf);
        client.setEndpoint(endpoint);
        client.setS3ClientOptions(new S3ClientOptions().withPathStyleAccess(pathStyleAccess));
        
        logger.debug("S3 client has been initialized");
    }
    
    @Override
    public void setAuthContext(AuthContext info) {
        super.setAuthContext(info);
//        try {
//        	client = (AmazonS3)info.get(S3CLIENT_KEY);
//            logger.debug("s3client=" + client);
//        } catch (Exception e) {
//            throw new StorageException(e);
//        }
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
        	
            S3Object s3Obj = client.getObject(container, object);
            stream = s3Obj.getObjectContent();
            
        } catch (Exception e) {
            throw new StorageException(e);
        }
        return stream;
    }

    @Override
    public void createContainer(String container, Config config) {
        super.createContainer(container, config);
        try {
        	if(!client.doesBucketExist(container)) {
	        	
	            client.createBucket(container);
        	}
        } catch (Exception e) {
            throw new StorageException(e);
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
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public void deleteContainer(String container, Config config) {
        super.deleteContainer(container, config);
        try {
        	if(client.doesBucketExist(container)) {
        		client.deleteBucket(container);
        	}
        } catch(AmazonS3Exception awse) {
        	if(awse.getStatusCode() != HttpStatus.SC_NOT_FOUND) {
        		throw new StorageException(awse);
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
        } catch(AmazonS3Exception awse) {
        	if(awse.getStatusCode() != HttpStatus.SC_NOT_FOUND) {
        		throw new StorageException(awse);
        	}
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

}
