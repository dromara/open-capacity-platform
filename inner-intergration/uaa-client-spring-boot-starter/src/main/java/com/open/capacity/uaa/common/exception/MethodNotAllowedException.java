package com.open.capacity.uaa.common.exception;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
* 自定义DefaultOAuth2Exception
*
* @author owen
* @date 2018/8/5
* blog: https://blog.51cto.com/13005375 
* code: https://gitee.com/owenwangwen/open-capacity-platform
*/
@SuppressWarnings("all")
@JsonSerialize(using = OAuth2ExceptionSerializer.class)
public class MethodNotAllowedException extends DefaultOAuth2Exception {

	public MethodNotAllowedException(String msg, Throwable t) {
		super(msg);
	}

	@Override
	public String getOAuth2ErrorCode() {
		return "method_not_allowed";
	}

	@Override
	public int getHttpErrorCode() {
		return HttpStatus.METHOD_NOT_ALLOWED.value();
	}

}
