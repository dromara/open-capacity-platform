package com.open.capacity.common.service;

import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface GenericService {

	@PostMapping("{url}")
	public <T> String post(@PathVariable("url") String url, @RequestBody T params);

	@GetMapping("{url}")
	public <T> String get(@PathVariable("url") String url, @SpringQueryMap T params);

}
