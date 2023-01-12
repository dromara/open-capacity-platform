package com.open.capacity.gateway.utils;

import cn.hutool.core.util.StrUtil;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * @author someday
 * @date 2019/10/7 code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@Slf4j
@UtilityClass
public class ReactiveAddrUtil {
	private final static String UNKNOWN_STR = "unknown";

	/**
	 * 获取客户端IP地址
	 */
	public String getRemoteAddr(ServerHttpRequest request) {
		Map<String, String> headers = request.getHeaders().toSingleValueMap();
		String ip = headers.get("X-Forwarded-For");
		if (isEmptyIP(ip)) {
			ip = headers.get("Proxy-Client-IP");
			if (isEmptyIP(ip)) {
				ip = headers.get("WL-Proxy-Client-IP");
				if (isEmptyIP(ip)) {
					ip = headers.get("HTTP_CLIENT_IP");
					if (isEmptyIP(ip)) {
						ip = headers.get("HTTP_X_FORWARDED_FOR");
						if (isEmptyIP(ip)) {
							ip = request.getRemoteAddress().getAddress().getHostAddress();
							if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
								// 根据网卡取本机配置的IP
								ip = getLocalAddr();
							}
						}
					}
				}
			}
		} else if (ip.length() > 15) {
			String[] ips = ip.split(",");
			for (int index = 0; index < ips.length; index++) {
				String strIp = ips[index];
				if (!isEmptyIP(ip)) {
					ip = strIp;
					break;
				}
			}
		}
		return ip;
	}

	private boolean isEmptyIP(String ip) {
		if (StrUtil.isEmpty(ip) || UNKNOWN_STR.equalsIgnoreCase(ip)) {
			return true;
		}
		return false;
	}

	/**
	 * 获取本机的IP地址
	 */
	public String getLocalAddr() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			log.error("InetAddress.getLocalHost()-error", e);
		}
		return "";
	}

	public String getBrowser(String browser) {
		if (StrUtil.isNotEmpty(browser)) {
			if (browser.contains("CHROME")) {
				return "CHROME";
			} else if (browser.contains("FIREFOX")) {
				return "FIREFOX";
			} else if (browser.contains("SAFARI")) {
				return "SAFARI";
			} else if (browser.contains("EDGE")) {
				return "EDGE";
			}
		}
		return browser;
	}

	public String getOperatingSystem(String operatingSystem) {
		if (StrUtil.isNotEmpty(operatingSystem)) {
			if (operatingSystem.contains("MAC_OS_X")) {
				return "MAC_OS_X";
			} else if (operatingSystem.contains("ANDROID")) {
				return "ANDROID";
			}
		}
		return operatingSystem;
	}
}
