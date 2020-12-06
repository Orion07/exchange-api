package com.orion.exchangeapi.config;

import com.orion.exchangeapi.constants.ProjectConstants;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpClientConfig {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @Bean
    public HttpClient getHttpClient() {
        HttpClientBuilder builder = HttpClientBuilder.create();

        RequestConfig config = RequestConfig.custom()
                .setConnectionRequestTimeout(ProjectConstants.HTTP_REQUEST_TIMEOUT * 1000)
                .setSocketTimeout(ProjectConstants.HTTP_REQUEST_TIMEOUT * 1000)
                .setConnectTimeout(ProjectConstants.HTTP_REQUEST_TIMEOUT * 1000)
                .build();

        SSLContextBuilder sslBuilder = new SSLContextBuilder();
        try {
            sslBuilder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(
                    sslBuilder.build());

            builder.setSSLSocketFactory(socketFactory);
        } catch (Exception e) {
            LOG.error("::getHttpClient", e);
        }

        CloseableHttpClient httpClient = builder.setDefaultRequestConfig(config).build();
        return httpClient;
    }
}
