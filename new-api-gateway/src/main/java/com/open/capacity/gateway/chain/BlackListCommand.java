package com.open.capacity.gateway.chain;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import com.open.capacity.common.dto.ResponseEntity;
import com.open.capacity.common.properties.BlackListProperties;
import com.open.capacity.common.utils.IpAddressMatcher;
import com.open.capacity.gateway.context.SecurityContext;

import cn.hutool.http.HttpStatus;

/**
 * 
 * 黑名单命令 禁止黑名单用户范围
 */
@Component
public class BlackListCommand implements Command {

	@Autowired
	private BlackListProperties blackListProperties;

	@Override
	public boolean execute(Context context) throws Exception {

		if (blackListProperties.getEnable()) {
			SecurityContext securityContext = (SecurityContext) context;
			ServerHttpRequest request = securityContext.getExchange().getRequest();
			URI uri = request.getURI();
			String path = uri.getPath();
			InetSocketAddress remoteAddress = request.getRemoteAddress();
			if (remoteAddress == null) {
				securityContext.setCode(HttpStatus.HTTP_UNAUTHORIZED);
				securityContext.setEntity(ResponseEntity.failed("当前IP已被禁用!"));
				securityContext.setResult(true);
				return true;
			}
			InetAddress address = remoteAddress.getAddress();
			String hostAddress = address.getHostAddress();
			List<String> ipList = blackListProperties.getIpList();
			for (String ip : ipList) {
				IpAddressMatcher ipAddressMatcher = new IpAddressMatcher(ip);
				boolean matches = ipAddressMatcher.matches(hostAddress);
				if (matches) {
					securityContext.setCode(HttpStatus.HTTP_UNAUTHORIZED);
					securityContext.setEntity(ResponseEntity.failed("当前IP已被禁用!"));
					securityContext.setResult(true);
					return true;
				}
			}
			AntPathMatcher antPathMatcher = new AntPathMatcher();
			List<BlackListProperties.BlackList> services = blackListProperties.getServices();
			for (BlackListProperties.BlackList service : services) {
				String name = service.getName();
				List<String> pathList = service.getPathList();
				for (String p : pathList) {
					String pattern = name.startsWith("/") ? name : "/" + name;
					pattern = p.startsWith("/") ? pattern + p : pattern + "/" + p;
					boolean match = antPathMatcher.match(pattern, path);
					if (match) {
						securityContext.setCode(HttpStatus.HTTP_UNAUTHORIZED);
						securityContext.setEntity(ResponseEntity.failed("当前IP访问的目标地址已被禁用!"));
						securityContext.setResult(true);
						return true;
					}
				}
			}
		}

		return false;
	}

}
