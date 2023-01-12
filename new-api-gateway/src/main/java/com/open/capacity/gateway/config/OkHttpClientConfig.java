package com.open.capacity.gateway.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import okhttp3.OkHttpClient;

@Configuration
public class OkHttpClientConfig {
	@Bean
	@LoadBalanced
	public WebClient.Builder webClientBuilder() {
		return WebClient.builder();
	}

	@Bean
	@LoadBalanced
	public OkHttpClient.Builder okHttpBuilder() {
		return new OkHttpClient.Builder();
	}
}