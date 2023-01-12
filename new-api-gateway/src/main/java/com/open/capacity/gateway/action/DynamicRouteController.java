package com.open.capacity.gateway.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.open.capacity.common.dto.ResponseEntity;
import com.open.capacity.gateway.route.DynamicRouteDefinitionRepository;

import reactor.core.publisher.Mono;

/**
 * 
 * @author owen
 * 动态路由处理类
 *
 */
@RestController
@RequestMapping("/routes/*")
public class DynamicRouteController {

	@Autowired
	private DynamicRouteDefinitionRepository dynamicRouteDefinitionRepository;
	
	@PostMapping("add")
	public  Mono<ResponseEntity<Object>>  add(@RequestBody RouteDefinition routeDefinition) {
		return dynamicRouteDefinitionRepository.add(routeDefinition);
	}
	
	@PostMapping("delete")
	public  Mono<ResponseEntity<Object>> delete(@RequestBody RouteDefinition routeDefinition) {
		return dynamicRouteDefinitionRepository.delete(routeDefinition);
	}
	
	@PostMapping("update")
	public  Mono<ResponseEntity<Object>>  update(@RequestBody RouteDefinition routeDefinition) {
		return dynamicRouteDefinitionRepository.update(routeDefinition);
	}
	
}
