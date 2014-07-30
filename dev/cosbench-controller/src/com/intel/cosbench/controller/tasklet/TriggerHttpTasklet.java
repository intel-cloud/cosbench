package com.intel.cosbench.controller.tasklet;

import java.io.IOException;
import java.net.SocketTimeoutException;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.DeserializationConfig.Feature;

import com.intel.cosbench.client.http.HttpClientUtil;
import com.intel.cosbench.controller.model.DriverContext;
import com.intel.cosbench.log.LogFactory;
import com.intel.cosbench.log.Logger;
import com.intel.cosbench.protocol.TriggerResponse;
import com.intel.cosbench.service.UnexpectedException;

abstract class TriggerHttpTasklet implements Tasklet{
	private transient HttpClient httpClient;
    private transient ObjectMapper mapper;
    protected DriverContext driver;
    protected String trigger = null;
    protected boolean isEnable; // true=enableTrigger; false=killTrigger
    protected String scriptName = null;
    protected String wsId = null;

	Class<TriggerResponse> clazz = TriggerResponse.class;
    
    private static final int TIMEOUT = 300 * 1000;
    protected static final Logger LOGGER = LogFactory.getSystemLogger();
    
    protected abstract void execute();
    protected abstract void handleResponse(TriggerResponse response);
    
    public TriggerHttpTasklet(DriverContext driver, String trigger, boolean option, String wsId) {
		this.driver = driver;
		this.trigger = trigger;
		this.isEnable = option;
		this.wsId = wsId;		
	}
    
    protected void initObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        DeserializationConfig config = mapper.copyDeserializationConfig();
        config.disable(Feature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setDeserializationConfig(config);
        this.mapper = mapper;
    }

    protected void initHttpClient() {
        HttpClient client = HttpClientUtil.createHttpClient(TIMEOUT);
        this.httpClient = client;
    }

    protected synchronized void closeHttpClient() {
        HttpClient client = this.httpClient;
        HttpClientUtil.disposeHttpClient(client);
    }

    private String issueHttpRequest(String command, String content) {
        String url = driver.getUrl() + "/i/" + command + ".command";
        HttpClient client = this.httpClient;
        HttpPost request = prepareRequest(content, url);
        String body = null;
        try {
            HttpResponse response = client.execute(request);
            body = fetchResponseBody(response);
        } catch (SocketTimeoutException ste) {
            LOGGER.error("fail to POST driver while execute trigger");
        } catch (ConnectTimeoutException cte) {
            LOGGER.error("fail to POST driver while execute trigger");
        } catch (Exception e) {
            LOGGER.error("fail to POST driver while execute trigger");
        }
        return body; // HTTP response body retrieved
    }

    private static HttpPost prepareRequest(String content, String url) {
        HttpPost POST = new HttpPost(url);
        try {
            if (StringUtils.isNotEmpty(content))
                POST.setEntity(new StringEntity(content));
        } catch (Exception e) {
            throw new UnexpectedException(e); // will not happen
        }
        if (content != null && content.length() > 0)
            if (!content.startsWith("<?xml"))
                LOGGER.debug("[ >> ] - {} -> {}", content, url);
            else
                LOGGER.debug("[ >> ] - [xml-content] -> {}", url);
        else
            LOGGER.debug("[ >> ] - [empty-body] -> {}", url);
        return POST; // HTTP request prepared
    }

    private static String fetchResponseBody(HttpResponse response)
            throws IOException {
        String body = null;
        HttpEntity entity = response.getEntity();
        StatusLine status = response.getStatusLine();
        try {
            body = EntityUtils.toString(entity);
        } finally {
            EntityUtils.consume(entity);
        }
        if (body.length() < 2048)
            LOGGER.debug("[ << ] - {} {}", status, body);
        else
            LOGGER.debug("[ << ] - {} [body-omitted]", status);
        return body; // the response body
    }
    
    protected void issueCommand(String command, String content) {
        TriggerResponse response = null;
        String body = issueHttpRequest(command, content);
        if (body == null) {
        	LOGGER.error("TriggerResponse body is null");
        	return;
        }
        try {
            response = this.mapper.readValue(body, clazz);
        } catch (Exception e) {
            LOGGER.error("can not parse TriggerResponse body", e);
            return;
        }
        if (!response.isSucc()) {
            String msg = "driver report error: HTTP {} - {}";
            LOGGER.error(msg, response.getCode(), response.getError());
            return;
        }
        handleResponse(response);
    }
    
    @Override
    public Tasklet call() {
        try {
        	execute();
        } catch (Exception e) {
            LOGGER.error("unexpected exception of trigger", e);
        }
        return this; /* okay -- done */
    }

}

