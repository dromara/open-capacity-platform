package com.open.capacity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import com.open.capacity.common.feign.GlobalFeignConfig;
import com.open.capacity.common.lb.annotation.EnableFeignInterceptor;

/**
 * @author 作者 owen E-mail: 624191343@qq.com
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignInterceptor
@EnableFeignClients(defaultConfiguration= GlobalFeignConfig.class)
public class UserCenterApp {
    public static void main(String[] args) {
        SpringApplication.run(UserCenterApp.class, args);
    }
}
