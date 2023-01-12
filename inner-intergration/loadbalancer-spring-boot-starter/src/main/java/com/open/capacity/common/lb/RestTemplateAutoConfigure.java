package com.open.capacity.common.lb;

import java.util.concurrent.TimeUnit;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.open.capacity.common.lb.config.RestTemplateProperties;
import com.open.capacity.common.lb.strategy.CustomConnectionKeepAliveStrategy;

/**
 * @author owen
 * @date 2018/11/17
 *  code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@EnableConfigurationProperties(RestTemplateProperties.class)
public class RestTemplateAutoConfigure {
    @Autowired
    private RestTemplateProperties restTemplateProperties;

    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory httpRequestFactory) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(httpRequestFactory);
        return restTemplate;
    }

    /**
     * httpclient 实现的ClientHttpRequestFactory
     */
    @Bean
    public ClientHttpRequestFactory httpRequestFactory(HttpClient httpClient) {
    	
    	HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(
				httpClient);
//		 连接超时
		clientHttpRequestFactory.setConnectTimeout(restTemplateProperties.getConnectTimeout());
//		 数据读取超时时间，即SocketTimeout
		clientHttpRequestFactory.setReadTimeout(restTemplateProperties.getReadTimeout());
//		连接不够用的等待时间，不宜过长，必须设置，比如连接不够用时，时间过长将是灾难性的
		clientHttpRequestFactory.setConnectionRequestTimeout(restTemplateProperties.getConnectionRequestTimeout());
//		缓冲请求数据，默认值是true。通过POST或者PUT大量发送数据时，建议将此属性更改为false，以免耗尽内存。
		clientHttpRequestFactory.setBufferRequestBody(false);
        return clientHttpRequestFactory;
    }

    /**
     * 使用连接池的 httpclient
     */
    @Bean
    public HttpClient httpClient() {
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", SSLConnectionSocketFactory.getSocketFactory())
                .build();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);
        // 最大链接数1000
        connectionManager.setMaxTotal(restTemplateProperties.getMaxTotal());
        // 同路由并发数1000
        connectionManager.setDefaultMaxPerRoute(restTemplateProperties.getMaxPerRoute());

        RequestConfig requestConfig = RequestConfig.custom()
                // 读超时
                .setSocketTimeout(restTemplateProperties.getReadTimeout())
                // 链接超时
                .setConnectTimeout(restTemplateProperties.getConnectTimeout())
                // 链接不够用的等待时间
                .setConnectionRequestTimeout(restTemplateProperties.getReadTimeout())
                .build();

        return HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(connectionManager)
                .evictIdleConnections(30, TimeUnit.SECONDS)
                .setRetryHandler(new DefaultHttpRequestRetryHandler(3, true))    //重试次数，默认是3次，没有开启
                .setKeepAliveStrategy(DefaultConnectionKeepAliveStrategy.INSTANCE) //使用请求Keep-Alive的值，没有的话永久有效
                .setKeepAliveStrategy(new CustomConnectionKeepAliveStrategy()) // 自定义
                .disableAutomaticRetries().build();
    }
}
