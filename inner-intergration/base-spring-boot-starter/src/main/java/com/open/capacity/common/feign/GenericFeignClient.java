package com.open.capacity.common.feign;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.open.capacity.common.feign.factory.GenericFeignClientFactory;
import com.open.capacity.common.service.GenericService;

@Component
public class GenericFeignClient {

	@Autowired
	private GenericFeignClientFactory<GenericService> dynamicFeignClientFactory;

	public <T> String post(String serviceId, String url, T params) {
		GenericService dynamicService = dynamicFeignClientFactory.getFeignClient(GenericService.class, serviceId);
		return dynamicService.post(url, params);
	}

	public <T> String get(String serviceId, String url, T params) {
		GenericService dynamicService = dynamicFeignClientFactory.getFeignClient(GenericService.class, serviceId);
		return dynamicService.get(url, params);
		 
	}

}
