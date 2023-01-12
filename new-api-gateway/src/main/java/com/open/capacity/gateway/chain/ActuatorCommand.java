package com.open.capacity.gateway.chain;

import java.net.URI;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import com.google.common.collect.Lists;
import com.open.capacity.common.dto.ResponseEntity;
import com.open.capacity.common.properties.SecurityProperties;
import com.open.capacity.gateway.context.SecurityContext;

import cn.hutool.http.HttpStatus;

/**
 * 
 * 端点命令 禁止通过网关访问服务的端点
 */
@Component
public class ActuatorCommand implements Command {

	@Autowired
	private SecurityProperties securityProperties;

	private static final List<String> list = Lists.newArrayList();

	static {
		list.add("/actuator/**");
		list.add("/*/actuator/**");
	}

	@Override
	public boolean execute(Context context) throws Exception {

		if (!securityProperties.getActuator().getEnable()) {
			SecurityContext securityContext = (SecurityContext) context;
			ServerHttpRequest request = securityContext.getExchange().getRequest();
			URI uri = request.getURI();
			String path = uri.getPath();
			AntPathMatcher antPathMatcher = new AntPathMatcher();
			boolean matchActuator = list.stream().anyMatch(item -> antPathMatcher.match(item, path));
			if (matchActuator) {
				securityContext.setCode(HttpStatus.HTTP_UNAUTHORIZED);
				securityContext.setEntity(ResponseEntity.failed("禁止通过网关访问服务端点!"));
				securityContext.setResult(true);
				return true;
			}
		}

		return false;
	}

}
