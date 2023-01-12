package com.open.capacity.common.utils;

import java.security.MessageDigest;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.open.capacity.common.dto.DeviceInfo;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DeviceUtil {

	/**
	 * 生成web设备唯一ID
	 * 
	 * @param map
	 * @return
	 */
	public String geneWebUniqueDeviceId(Map<String, String> map) {
		String deviceId = md5(map.toString());
		return deviceId;
	}

	/**
	 * MD5加密
	 *
	 * @param data
	 * @return
	 */
	public String md5(String data) {
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

	/**
	 * 获取浏览器对象
	 * 
	 * @param agent
	 * @return
	 */
	public Browser getBrowser(String agent) {
		UserAgent userAgent = UserAgent.parseUserAgentString(agent);
		return userAgent.getBrowser();

	}

	/**
	 * 获取操作系统
	 * 
	 * @param agent
	 * @return
	 */
	public OperatingSystem getOperationSystem(String agent) {
		UserAgent userAgent = UserAgent.parseUserAgentString(agent);
		return userAgent.getOperatingSystem();
	}

	/**
	 * 获取浏览器名称
	 * 
	 * @param agent
	 * @return Firefox、Chrome
	 */
	public String getBrowserName(String agent) {

		return getBrowser(agent).getGroup().getName();

	}

	/**
	 * 获取设备类型
	 * 
	 * @param agent
	 * @return MOBILE、COMPUTER
	 */
	public String getDeviceType(String agent) {
		return getOperationSystem(agent).getDeviceType().toString();
	}

	/**
	 * 获取os: windwos、IOS、Android
	 * 
	 * @param agent
	 * @return
	 */
	public String getOS(String agent) {
		return getOperationSystem(agent).getGroup().getName();
	}

	/**
	 * 获取设备厂家
	 * 
	 * @param agent
	 * @return
	 */
	public String getDeviceManufacturer(String agent) {
		return getOperationSystem(agent).getManufacturer().toString();
	}

	/**
	 * 操作系统版本
	 * 
	 * @param userAgent
	 * @return Android 1.x、Intel Mac OS X 10.15
	 */
	public String getOSVersion(String userAgent) {
		String osVersion = "";
		if (StringUtils.isBlank(userAgent)) {
			return osVersion;
		}
		String[] strArr = userAgent.substring(userAgent.indexOf("(") + 1, userAgent.indexOf(")")).split(";");
		if (null == strArr || strArr.length == 0) {
			return osVersion;
		}

		osVersion = strArr[1];
		return osVersion;
	}

	/**
	 * 解析对象
	 * 
	 * @param agent
	 * @return
	 */
	public DeviceInfo getDeviceInfo(String agent) {

		UserAgent userAgent = UserAgent.parseUserAgentString(agent);
		Browser browser = userAgent.getBrowser();
		OperatingSystem operatingSystem = userAgent.getOperatingSystem();

		String browserName = browser.getGroup().getName();
		String os = operatingSystem.getGroup().getName();
		String manufacture = operatingSystem.getManufacturer().toString();
		String deviceType = operatingSystem.getDeviceType().toString();

		DeviceInfo deviceInfoDO = DeviceInfo.builder().browserName(browserName).deviceManufacturer(manufacture)
				.deviceType(deviceType).os(os).osVersion(getOSVersion(agent)).build();

		return deviceInfoDO;
	}

//	public static void main(String[] args) {
//
//		String userAgentStr = "Mozilla/5.0 (Linux; Android 10; LIO-AN00 Build/HUAWEILIO-AN00; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/77.0.3865.120 MQQBrowser/6.2 TBS/045713 Mobile Safari/537.36 MMWEBID/3189 MicroMessenger/8.0.11.1980(0x28000B51) Process/tools WeChat/arm64 Weixin NetType/WIFI Language/zh_CN ABI/arm64";
//
//		UserAgent userAgent = UserAgent.parseUserAgentString(userAgentStr);
//		Browser browser = userAgent.getBrowser();
//		OperatingSystem operatingSystem = userAgent.getOperatingSystem();
//
//		String browserName = browser.getGroup().getName();
//		String os = operatingSystem.getGroup().getName();
//		String manufacture = operatingSystem.getManufacturer().getName();
//		String deviceType = operatingSystem.getDeviceType().getName();
//
//		System.out.println("browserName=" + browserName + ",os=" + os + ",manufacture=" + manufacture + ",deviceType="
//				+ deviceType);
//
//	}

}
