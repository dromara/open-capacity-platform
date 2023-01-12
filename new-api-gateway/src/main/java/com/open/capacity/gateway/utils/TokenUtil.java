package com.open.capacity.gateway.utils;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;

import com.open.capacity.common.constant.CommonConstant;
import com.open.capacity.common.utils.StringUtil;

public class TokenUtil {
	public static String extractToken(ServerHttpRequest request) {
		List<String> strings = request.getHeaders().get(CommonConstant.TOKEN_HEADER);
		String authToken = "";
		if (!StringUtil.isEmpty(strings) && strings.get(0).contains(CommonConstant.BEARER_TYPE)) {
			authToken = strings.get(0).substring(CommonConstant.BEARER_TYPE.length()).trim();
		}
		if (StringUtils.isBlank(authToken)) {
			strings = request.getQueryParams().get(CommonConstant.ACCESS_TOKEN);
			if (!StringUtil.isEmpty(strings)) {
				authToken = strings.get(0);
			}
		}
		return authToken;
	}
}
