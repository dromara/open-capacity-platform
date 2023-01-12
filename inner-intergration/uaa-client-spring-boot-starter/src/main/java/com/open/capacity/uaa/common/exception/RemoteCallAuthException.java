package com.open.capacity.uaa.common.exception;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.springframework.http.HttpStatus;

@SuppressWarnings("all")
@JsonSerialize(using = OAuth2ExceptionSerializer.class)
public class RemoteCallAuthException extends DefaultOAuth2Exception {

	public RemoteCallAuthException(String msg, Throwable t) {
		super(msg);
	}

	@Override
	public String getOAuth2ErrorCode() {
		return "SERVICE_UNAVAILABLE";
	}

	@Override
	public int getHttpErrorCode() {
		return HttpStatus.SERVICE_UNAVAILABLE.value();
	}

}