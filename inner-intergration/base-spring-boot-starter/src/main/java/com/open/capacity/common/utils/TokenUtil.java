package com.open.capacity.common.utils;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.open.capacity.common.constant.CommonConstant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TokenUtil {

	public String getToken() {
		String token = "";
		try {
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
					.getRequest();
			String header = request.getHeader(CommonConstant.TOKEN_HEADER);
			token = StringUtil.isBlank(StringUtil.substringAfter(header, CommonConstant.BEARER_TYPE + " "))
					? request.getParameter(CommonConstant.ACCESS_TOKEN)
					: StringUtil.substringAfter(header, CommonConstant.BEARER_TYPE + " ");
			token = StringUtil.isBlank(request.getHeader(CommonConstant.TOKEN_HEADER)) ? token
					: request.getHeader(CommonConstant.TOKEN_HEADER);
		} catch (IllegalStateException e) {
		}
		return token;
	}

}
