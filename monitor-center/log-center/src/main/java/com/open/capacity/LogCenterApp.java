package com.open.capacity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import com.open.capacity.common.feign.GlobalFeignConfig;
import com.open.capacity.common.lb.annotation.EnableFeignInterceptor;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignInterceptor
@EnableFeignClients(defaultConfiguration= GlobalFeignConfig.class)
public class LogCenterApp {
	public static void main(String[] args) {
		SpringApplication.run(LogCenterApp.class, args);
	}
}
