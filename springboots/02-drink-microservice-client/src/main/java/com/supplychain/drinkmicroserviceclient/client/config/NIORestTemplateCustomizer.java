package com.supplychain.drinkmicroserviceclient.client.config;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.reactor.IOReactorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsAsyncClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
// if enabled this config get picked up for the RestTemplate otherwise the default implementation which is slow
public class NIORestTemplateCustomizer implements RestTemplateCustomizer {
    private final Integer connectionRequestTimeout;
    private final Integer socketTimeout;
    private final Integer threadCount;

    public NIORestTemplateCustomizer(@Value("${supplychain.restclientnio.connection_request_timeout}") Integer connectionRequestTimeout,
                                     @Value("${supplychain.restclientnio.thread_count}") Integer socketTimeout,
                                     @Value("${supplychain.restclientnio.socket_timeout}") Integer threadCount) {
        this.connectionRequestTimeout = connectionRequestTimeout;
        this.socketTimeout = socketTimeout;
        this.threadCount = threadCount;
    }

    public ClientHttpRequestFactory clientHttpRequestFactory() throws IOReactorException {
        final DefaultConnectingIOReactor ioreactor = new DefaultConnectingIOReactor(IOReactorConfig.custom().
                setConnectTimeout(connectionRequestTimeout).
                setIoThreadCount(socketTimeout).
                setSoTimeout(threadCount).
                build());

        final PoolingNHttpClientConnectionManager connectionManager = new PoolingNHttpClientConnectionManager(ioreactor);
        connectionManager.setDefaultMaxPerRoute(100);
        connectionManager.setMaxTotal(1000);

        CloseableHttpAsyncClient httpAsyncClient = HttpAsyncClients.custom()
                .setConnectionManager(connectionManager)
                .build();

        //HttpComponentsAsyncClientHttpRequestFactory is DEPRECATED in favor of reactive client I would presume!
        return new HttpComponentsAsyncClientHttpRequestFactory(httpAsyncClient);

    }

    @Override
    public void customize(RestTemplate restTemplate) {
        try {
            restTemplate.setRequestFactory(clientHttpRequestFactory());
        } catch (IOReactorException e) {
            e.printStackTrace();
        }
    }
}