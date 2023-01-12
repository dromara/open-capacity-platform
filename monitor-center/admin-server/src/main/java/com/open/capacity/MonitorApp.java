package com.open.capacity;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** 
* @author 作者 owen E-mail: 624191343@qq.com
* @version 创建时间：2017年12月8日 上午9:03:32 
* 类说明 
*/
@EnableAdminServer
@SpringBootApplication
public class MonitorApp {
	public static void main(String[] args) {
		SpringApplication.run(MonitorApp.class, args);
	}

}
