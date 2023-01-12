package com.open.capacity.common.utils;

import cn.hutool.core.util.StrUtil;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;

/**
 * @author someday
 * @date 2018/9/8
 */
@Slf4j
@UtilityClass
public class AddrUtil {
	private final static String UNKNOWN_STR = "unknown";

	/**
	 * 获取客户端IP地址
	 */
	public String getRemoteAddr(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (isEmptyIP(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
			if (isEmptyIP(ip)) {
				ip = request.getHeader("WL-Proxy-Client-IP");
				if (isEmptyIP(ip)) {
					ip = request.getHeader("HTTP_CLIENT_IP");
					if (isEmptyIP(ip)) {
						ip = request.getHeader("HTTP_X_FORWARDED_FOR");
						if (isEmptyIP(ip)) {
							ip = request.getRemoteAddr();
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

	/**
	 * MD5加密
	 *
	 * @param data
	 * @return
	 */
	public String MD5(String data) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] array = md.digest(data.getBytes("UTF-8"));
			StringBuilder sb = new StringBuilder();
			for (byte item : array) {
				sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
			}

			return sb.toString().toUpperCase();
		} catch (Exception exception) {
		}
		return null;

	}
}
