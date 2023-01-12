package com.open.capacity.uaa.google;

import java.lang.reflect.UndeclaredThrowableException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.open.capacity.common.algorithm.Base32Util;
import com.open.capacity.common.algorithm.HexEncoding;

import io.vavr.control.Try;

/**
 * 二次验证工具类
 * 
 */
public class GoogleOTPAuthUtil {
	public static enum LEVEL {
		ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT
	}

	// 加密强度，对应 0 1 2 3 4 5 6 7 8 几个等级
	private static final int[] DIGITS_POWER = { 1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000 };

	// crypto加密算法
	private static final String[] CRYPTOS = { "HmacSHA1", "HmacSHA256", "HmacSHA512" };

	// 日期格式
	private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	// 时区
	private static final String DATETIME_ZONE = "UTC";
	private static final String STEP_PREFIX = "0";
	// 步长
	private static final int STEP_LENGHT = 30;

	// 协议前缀字符串
	private static final String PROTOCOL_PREFIX = "otpauth://totp/";
	private static final String PROTOCOL_SUFFEX = "?secret=";

	/**
	 * HMAC通过crypto参数计算哈希值
	 * 
	 */
	private static byte[] hmac_sha(String crypto, byte[] keyBytes, byte[] text) {
		try {
			Mac hmac;
			hmac = Mac.getInstance(crypto);
			SecretKeySpec macKey = new SecretKeySpec(keyBytes, "RAW");
			hmac.init(macKey);
			return hmac.doFinal(text);
		} catch (GeneralSecurityException gse) {
			throw new UndeclaredThrowableException(gse);
		}
	}

	/**
	 * 转换16进制字符串为byte数组
	 * 
	 */
	private static byte[] hexStr2Bytes(String hex) {
		// Adding one byte to get the right conversion
		// Values starting with "0" can be converted
		byte[] bArray = new BigInteger("10" + hex, 16).toByteArray();

		// Copy all the REAL bytes, not the "first"
		byte[] ret = new byte[bArray.length - 1];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = bArray[i + 1];
		}

		return ret;
	}

	/**
	 * 创建TOTP值
	 * 
	 */
	public static String generateTOTP(String key, String time, int level, String crypto) {
		String result = null;
		// 使用计数器
		// 第一个8字节用于movingFactor（移动因子）
		// 基于 RFC 4226 (HOTP, https://tools.ietf.org/html/rfc4226)
		while (time.length() < 16) {
			time = STEP_PREFIX + time;
		}
		// 获取字节数组中的十六进制数值，并产生一个随机数
		// 随机数
		byte[] c = hexStr2Bytes(time);
		// 密钥
		byte[] k = hexStr2Bytes(key);
		// 核心方法
		byte[] hash = hmac_sha(crypto, k, c);
		// 将所选字节放入结果
		int offset = hash[hash.length - 1] & 0xf;
		int binary = ((hash[offset] & 0x7f) << 24) | ((hash[offset + 1] & 0xff) << 16)
				| ((hash[offset + 2] & 0xff) << 8) | (hash[offset + 3] & 0xff);
		int otp = binary % DIGITS_POWER[level];
		result = Integer.toString(otp);
		while (result.length() < level) {
			result = STEP_PREFIX + result;
		}
		return result;
	}

	/**
	 * 验证动态口令是否正确
	 * 
	 */
	public static boolean verify(String secret, String code) {
		return Try.of(() -> generateCode(secret).equals(code)).getOrElse(false);
	}

	/**
	 * 生成totp协议字符串
	 * 
	 */
	public static String generateTotpURI(String account, String secret) {
		return PROTOCOL_PREFIX + account + PROTOCOL_SUFFEX + secret;
	}

	/**
	 * 根据密钥生成动态验证码
	 * 
	 */
	public static String generateCode(String secret) {
		String secretHex = "";
		try {
			// 将解码出来的secret再进行十六进制编码
			secretHex = HexEncoding.encode(Base32Util.decode(secret));
		} catch (Exception e) {
			throw new RuntimeException();
		}
		// 步长
		// 不同厂家使用的时间步数不同
		// Google的身份验证器的时间步数是30秒
		// 阿里巴巴的身份宝使用的时间步数是30秒
		// 腾讯的Token时间步数是60秒
		String steps = STEP_PREFIX;
		// 日期格式与时区
		DateFormat df = new SimpleDateFormat(DATETIME_FORMAT);
		df.setTimeZone(TimeZone.getTimeZone(DATETIME_ZONE));

		// 当前时间
		long currentTime = System.currentTimeMillis() / 1_000L;
		try {
			// 依据当前时间戳获取动态密码计算的随机数
			long t = currentTime / STEP_LENGHT;
			steps = Long.toHexString(t).toUpperCase();
			// 不足16位前面补0
			while (steps.length() < 16) {
				steps = STEP_PREFIX + steps;
			}
			// 生成TOTP值
			return generateTOTP(secretHex, steps, LEVEL.SIX.ordinal(), CRYPTOS[0]);
		} catch (final Exception e) {
			throw new RuntimeException();
		}
	}

	/**
	 * 生成base32编码的随机密钥
	 * 
	 */
	public static String generateSecret(int length) {
		SecureRandom random = new SecureRandom();
		// 加盐
		byte[] salt = new byte[length / 2];
		random.nextBytes(salt);
		return Base32Util.encode(salt);
	}
}
