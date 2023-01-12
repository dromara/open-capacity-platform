package com.open.capacity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import com.open.capacity.common.feign.GlobalFeignConfig;
import com.open.capacity.common.lb.annotation.EnableFeignInterceptor;


/** 
 * @author owen 624191343@qq.com
 * @version 创建时间：2017年11月12日 上午22:57:51
 * 类说明 
*/
@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignInterceptor
@EnableFeignClients(defaultConfiguration= GlobalFeignConfig.class)
public class AuthServerApp {
	
	public static void main(String[] args) {
//		固定端口启动
//		SpringApplication.run(OpenAuthServerApp.class, args);
		
		
		SpringApplication app = new SpringApplication(AuthServerApp.class);
//		//随机端口启动
//        app.addListeners(new PortApplicationEnvironmentPreparedEventListener());
        app.run(args);
		
	}

}
 