package com.orion.exchangeapi.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orion.exchangeapi.constants.ProjectConstants;
import com.orion.exchangeapi.service.HttpService;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.net.URI;
import java.util.Map;

@Service
public class ApacheHttpService implements HttpService {

    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ApacheHttpService(HttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public <T> T get(URI uri, Map<String, String> headers, Class<T> responseClass) {
        HttpGet httpGet = new HttpGet(uri);
        prepareHeaders(httpGet, headers);

        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);
            String responseBody = EntityUtils.toString(httpResponse.getEntity(), ProjectConstants.CHARSET);
            LOG.info("::get uri:{}, response:{}", uri, responseBody);
            return objectMapper.readValue(responseBody, responseClass);
        } catch (Exception e) {
            LOG.error("::get uri:{}", uri, e);
        }

        return null;
    }

    private void prepareHeaders(HttpUriRequest request, Map<String, String> headers) {
        if (CollectionUtils.isEmpty(headers)) {
            return;
        }

        for (Map.Entry<String, String> header : headers.entrySet()) {
            request.addHeader(header.getKey(), header.getValue());
        }
    }
}
