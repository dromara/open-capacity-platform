package com.open.capacity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import com.open.capacity.common.feign.GlobalFeignConfig;
import com.open.capacity.common.lb.annotation.EnableBaseFeignInterceptor;
import com.open.capacity.gateway.annotation.EnableNacosDynamicRoute;

/**
 * @author someday
 * @date 2019/10/5
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableNacosDynamicRoute
@EnableBaseFeignInterceptor
@EnableFeignClients(defaultConfiguration= GlobalFeignConfig.class)
public class ApiGateWayApp  {
    public static void main(String[] args) {
        SpringApplication.run(ApiGateWayApp.class, args);
    }
}