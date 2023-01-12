package com.open.capacity.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CheckUtil {

	/**
	 * 邮箱正则
	 */
	private static final Pattern MAIL_PATTERN = Pattern
			.compile("^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$");

	/**
	 * 手机号正则
	 */
	private static final Pattern PHONE_PATTERN = Pattern
			.compile("^((13[0-9])|(14[0-9])|(15[0-9])|(17[0-9])|(18[0-9]))\\d{8}$");

	/**
	 * @param email
	 * @return
	 */
	public boolean isEmail(String email) {
		if (null == email || "".equals(email)) {
			return false;
		}
		Matcher m = MAIL_PATTERN.matcher(email);
		return m.matches();
	}

	/**
	 * @param phone
	 * @return
	 */
	public boolean isPhone(String phone) {
		if (null == phone || "".equals(phone)) {
			return false;
		}
		Matcher m = PHONE_PATTERN.matcher(phone);
		boolean result = m.matches();
		return result;

	}
}