package com.open.capacity.uaa.common.exception;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.open.capacity.common.constant.CommonConstant;
import com.open.capacity.common.utils.ResponseUtil;


/**
 * 请求处理类异常
 *
 * @author owen
 * @date 2018/8/5
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
public class DefaultAuthenticationEntryPoint implements AuthenticationEntryPoint {
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException {
		Map<String, String> rsp = new HashMap<>();
		rsp.put(CommonConstant.STATUS, HttpStatus.UNAUTHORIZED.value() + "");
		rsp.put("msg", "client_id或client_secret错误");
		ResponseUtil.renderJsonError(response, rsp, HttpStatus.UNAUTHORIZED.value());
	}

}
