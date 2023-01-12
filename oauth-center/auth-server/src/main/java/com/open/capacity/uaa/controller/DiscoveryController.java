package com.open.capacity.uaa.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.ListView;

import lombok.SneakyThrows;


/**
 * 服务管理
 * @author someday 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@RequestMapping("actuator")
@RestController
public class DiscoveryController {

	@Autowired
	@NacosInjected
	private NacosServiceManager nacosServiceManager ;
	
	@Autowired
	private NacosDiscoveryProperties nacosDiscoveryProperties ;
	
	
	@SneakyThrows
	@GetMapping("/services")
	public ListView<String> getService(){
		
		return nacosServiceManager.getNamingService(nacosDiscoveryProperties.getNacosProperties()).getServicesOfServer(1, 100,nacosDiscoveryProperties.getGroup());
	}
	
	@SneakyThrows
	@GetMapping("/instances/{serviceName}")
	public List<Instance> getInstance(@PathVariable String serviceName){
		return nacosServiceManager.getNamingService(nacosDiscoveryProperties.getNacosProperties()).getAllInstances(serviceName) ;
	}
	
}
