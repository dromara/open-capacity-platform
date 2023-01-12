package com.open.capacity.gateway.chain;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import com.open.capacity.common.dto.ResponseEntity;
import com.open.capacity.common.properties.SecurityProperties;
import com.open.capacity.gateway.context.SecurityContext;
import com.open.capacity.gateway.service.IRateLimitService;
import com.open.capacity.gateway.utils.TokenUtil;

/**
 * 限流命令
 */
@Component
public class RateLimitCommand implements Command {

	@Autowired
	private IRateLimitService rateLimitService;

	@Autowired
	private SecurityProperties securityProperties;

	@Override
	public boolean execute(Context context) throws Exception {

		if (securityProperties.getRatelimit().getEnable()) {
			SecurityContext securityContext = (SecurityContext) context;
			ServerHttpRequest request = securityContext.getExchange().getRequest();
			String accessToken = TokenUtil.extractToken(request);
			String reqUrl = request.getPath().value();
			// 超额自增处理
			boolean flag = rateLimitService.checkRateLimit(reqUrl, accessToken);
			// 超额限流
			if (flag) {
				securityContext.setCode(HttpStatus.TOO_MANY_REQUESTS.value());
				securityContext.setEntity(ResponseEntity.failed("TOO MANY REQUESTS!"));
				securityContext.setResult(true);
				return true;
			}
		}

		return false;
	}

}
