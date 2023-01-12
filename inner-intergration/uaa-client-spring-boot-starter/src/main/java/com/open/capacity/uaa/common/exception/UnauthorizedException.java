package com.open.capacity.uaa.common.exception;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
* 自定义异常DefaultOAuth2Exception
*
* @author owen
* @date 2018/8/5
* blog: https://blog.51cto.com/13005375 
* code: https://gitee.com/owenwangwen/open-capacity-platform
*/
@SuppressWarnings("all")	
@JsonSerialize(using = OAuth2ExceptionSerializer.class)
public class UnauthorizedException extends DefaultOAuth2Exception {

	public UnauthorizedException(String msg, Throwable t) {
		super(msg);
	}

	@Override
	public String getOAuth2ErrorCode() {
		return "unauthorized";
	}

	@Override
	public int getHttpErrorCode() {
		return HttpStatus.UNAUTHORIZED.value();
	}

}
