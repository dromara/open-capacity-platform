package com.open.capacity.common.utils;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import cn.hutool.extra.servlet.ServletUtil;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class WebUtils extends org.springframework.web.util.WebUtils {
	private static final String BASIC_ = "Basic ";
	private static final String UNKNOWN = "unknown";
	private static final String PATTERN_IP_ADDRESS = "((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})(\\.((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})){3}";
	private static final String XML_HTTP_REQUEST = "XMLHttpRequest";
	private static final String X_REQUESTED_WITH = "X-Requested-With";
	private static final String CONTENT_TYPE_JSON = "application/json";

	/**
	 * 获取 HttpServletRequest
	 *
	 * @return {HttpServletRequest}
	 */
	public HttpServletRequest getRequest() {
		return RequestContextHolder.getRequestAttributes() == null ? null
				: ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
	}

	/**
	 * 获取ip
	 *
	 * @return {String}
	 */
	public String getIP() {
		return getIP(WebUtils.getRequest());
	}

	/**
	 * 获取ip
	 *
	 * @param request HttpServletRequest
	 * @return {String}
	 */
	public String getIP(HttpServletRequest request) {
		Assert.notNull(request, "HttpServletRequest is null");
		String ip = ServletUtil.getClientIP(request, "X-Requested-For");
		if (!ip.matches(PATTERN_IP_ADDRESS)) {
			ip = request.getRemoteAddr();
		}
		if ("127.0.0.1".equals(ip) || ip.contains(":")) {
			try {
				ip = InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException e) {
				ip = null;
			}
		}

		return StringUtil.isBlank(ip) ? null : ip.split(",")[0];
	}

	/**
	 * 获取ip
	 *
	 * @param request ServerHttpRequest
	 * @return {String}
	 */
	public String getIP(ServerHttpRequest request) {
		Assert.notNull(request, "ServerHttpRequest is null");
		String ip = request.getHeaders().getFirst("X-Requested-For");
		if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeaders().getFirst("X-Forwarded-For");
		}
		if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeaders().getFirst("Proxy-Client-IP");
		}
		if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeaders().getFirst("WL-Proxy-Client-IP");
		}
		if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeaders().getFirst("HTTP_CLIENT_IP");
		}
		if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeaders().getFirst("HTTP_X_FORWARDED_FOR");
		}
		if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
			InetSocketAddress remoteAddress = request.getRemoteAddress();
			if (remoteAddress == null) {
				return "";
			} else {
				InetAddress address = remoteAddress.getAddress();
				ip = address == null ? remoteAddress.getHostString() : address.getHostAddress();
			}
		}
		if ("127.0.0.1".equals(ip) || ip.contains(":")) {
			try {
				ip = InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException e) {
				ip = null;
			}
		}

		return StringUtils.isBlank(ip) ? null : ip.split(",")[0];
	}

	public static boolean isAjaxResponse(HttpServletRequest request) {
		return isAjaxRequest(request) || isContentTypeJson(request) || isPostRequest(request);
	}

	public static boolean isObjectRequest(HttpServletRequest request) {
		return isPostRequest(request) && isContentTypeJson(request);
	}

	public static boolean isObjectRequest(SavedRequest request) {
		return isPostRequest(request) && isContentTypeJson(request);
	}

	public static boolean isAjaxRequest(HttpServletRequest request) {
		return XML_HTTP_REQUEST.equals(request.getHeader(X_REQUESTED_WITH));
	}

	public static boolean isAjaxRequest(SavedRequest request) {
		return request.getHeaderValues(X_REQUESTED_WITH).contains(XML_HTTP_REQUEST);
	}

	public static boolean isContentTypeJson(HttpServletRequest request) {
		return request.getHeader(HttpHeaders.CONTENT_TYPE).contains(CONTENT_TYPE_JSON);
	}

	public static boolean isContentTypeJson(SavedRequest request) {
		return request.getHeaderValues(HttpHeaders.CONTENT_TYPE).contains(CONTENT_TYPE_JSON);
	}

	public static boolean isPostRequest(HttpServletRequest request) {
		return HttpMethod.POST.name().equals(request.getMethod());
	}

	public static boolean isPostRequest(SavedRequest request) {
		return HttpMethod.POST.name().equals(request.getMethod());
	}
}
