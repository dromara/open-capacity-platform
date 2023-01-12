package com.open.capacity.common.algorithm;

import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.open.capacity.common.exception.BusinessException;

import cn.hutool.core.codec.Base64;

public class AESUtil {

	private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
	private static final String ALGORITHM = "AES";
	private static final String CHARSET = "utf-8";

	private static final String REQUEST_BODY_DECRYPT_ERROR = "解密失败，加密值不合法!";
	private static final String REQUEST_DECRYPT_ERROR = "请求数据解密失败!";
	private static final String RESPONSE_BODY_ENCRYPT_ERROR = "加密失败，加密值不合法!";

	/**
	 * 加密
	 *
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String context, String key) throws Exception {
		byte[] decode = context.getBytes(CHARSET);
		byte[] bytes = createKeyAndIv(decode, Cipher.ENCRYPT_MODE, key);
		return Base64.encode(bytes);
	}

	/**
	 * 解密
	 *
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String context, String key) throws Exception {
		byte[] decode = Base64.decode(context);
		byte[] bytes = createKeyAndIv(decode, Cipher.DECRYPT_MODE, key);
		return new String(bytes, CHARSET);
	}

	/**
	 * 获取key & iv
	 *
	 * @param context
	 * @param opmode
	 * @return
	 * @throws Exception
	 */
	public static byte[] createKeyAndIv(byte[] context, int opmode, String key) throws Exception {
		byte[] keyBytes = key.getBytes(CHARSET);
		byte[] ivBytes = key.getBytes(CHARSET);
		return cipherFilter(context, opmode, keyBytes, ivBytes);
	}

	/**
	 * 执行操作
	 *
	 * @param context
	 * @param opmode
	 * @param key
	 * @param iv
	 * @return
	 * @throws Exception
	 */
	public static byte[] cipherFilter(byte[] context, int opmode, byte[] key, byte[] iv) throws Exception {
		Key secretKeySpec = new SecretKeySpec(key, ALGORITHM);
		AlgorithmParameterSpec ivParameterSpec = new IvParameterSpec(iv);
		Cipher cipher = Cipher.getInstance(TRANSFORMATION);
		cipher.init(opmode, secretKeySpec, ivParameterSpec);
		return cipher.doFinal(context);
	}

	/**
	 * 解密经过加密的数据，如果数据被篡改，解密会失败。保证了数据完整性
	 *
	 */
	public static String dataDecrypt(String data, String key) {
		if (data == null || key == null || key.length() != 16) {
			throw new BusinessException(REQUEST_BODY_DECRYPT_ERROR);
		}
		String str = "";
		try {
			str = decrypt(data, key);
		} catch (Exception e) {
			throw new BusinessException(REQUEST_DECRYPT_ERROR);
		}
		return str;
	}

	/**
	 * 加密
	 *
	 */
	public static String dataEncrypt(String data, String key) {
		if (data == null || key == null || key.length() != 16) {
			throw new BusinessException(RESPONSE_BODY_ENCRYPT_ERROR);
		}
		String str = "";
		try {
			str = encrypt(data, key);
		} catch (Exception e) {
			throw new BusinessException(RESPONSE_BODY_ENCRYPT_ERROR);
		}
		return str;
	}
//
//	/**
//	 * @param args
//	 */
//	public static void main(String[] args) throws Exception {
//		String key = "1111111222212345";
//		String context = "{\"username\":\"123456\"}";
//		String encrypt = encrypt(context, key);
//		String encrypt2 = "xKodmCkOeW2XIUzokCu++4OI9X21jY9Y76OiVUFClco=";
//		String decrypt = decrypt(encrypt2, key);
//		System.out.println(decrypt);
//	}

}
