package com.open.capacity.uaa.common.exception;

import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Getter;


/**
 * 自定义OAuth2Exception
 *
 * @author owen
 * @date 2018/8/5
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@SuppressWarnings("all")
@JsonSerialize(using = OAuth2ExceptionSerializer.class)
public class DefaultOAuth2Exception extends OAuth2Exception {

	@Getter
	private String errorCode;
	
	@Getter
	private Integer statusCodeValue;

	public DefaultOAuth2Exception(String msg) {
		super(msg);
	}

	public DefaultOAuth2Exception(String msg, String errorCode, Integer status) {
		super(msg);
		this.errorCode = errorCode;
		this.statusCodeValue = status ;
	}
}
