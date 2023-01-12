package com.open.capacity.common.encryptbody.handler;

import com.open.capacity.common.algorithm.AESUtil;
import com.open.capacity.common.algorithm.SM4Util;
import com.open.capacity.common.encryptbody.bean.EncryptAnnotationInfoBean;
import com.open.capacity.common.encryptbody.config.EncryptBodyConfig;
import com.open.capacity.common.encryptbody.enums.SHAEncryptType;
import com.open.capacity.common.encryptbody.util.CommonUtils;
import com.open.capacity.common.encryptbody.util.ShaEncryptUtil;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.RSA;

/**
 * 加密接口
 * 
 * @author licoy.cn
 * @version 2022/3/29
 *          code:https://gitee.com/licoy/encrypt-body-spring-boot-starter
 */
public interface EncryptHandler {
	String encrypt(EncryptAnnotationInfoBean infoBean, EncryptBodyConfig config, String formatStringBody);

	/**
	 * md5加密
	 * 
	 * @return
	 */
	public static EncryptHandler md5EncryptHandler() {
		return new EncryptHandler() {
			@Override
			public String encrypt(EncryptAnnotationInfoBean infoBean, EncryptBodyConfig config,
					String formatStringBody) {
				return SecureUtil.md5().digestHex(formatStringBody);
			}

		};
	}

	/**
	 * sha加密
	 * 
	 * @return
	 */
	public static EncryptHandler shaEncryptHandler() {
		return new EncryptHandler() {
			@Override
			public String encrypt(EncryptAnnotationInfoBean infoBean, EncryptBodyConfig config,
					String formatStringBody) {
				SHAEncryptType shaEncryptType = infoBean.getShaEncryptType();
				if (shaEncryptType == null) {
					shaEncryptType = SHAEncryptType.SHA256;
				}
				return ShaEncryptUtil.encrypt(formatStringBody, shaEncryptType);
			}

		};
	}

	/**
	 * des加密
	 * 
	 * @return
	 */
	public static EncryptHandler desEncryptHandler() {
		return new EncryptHandler() {
			@Override
			public String encrypt(EncryptAnnotationInfoBean infoBean, EncryptBodyConfig config,
					String formatStringBody) {
				String key = CommonUtils.checkAndGetKey(config.getDesKey(), infoBean.getKey(), "DES-KEY");
				return SecureUtil.des(key.getBytes()).decryptStr(formatStringBody);
			}

		};
	}

	/**
	 * aes加密
	 * 
	 * @return
	 */
	public static EncryptHandler aesEncryptHandler() {
		return new EncryptHandler() {
			@Override
			public String encrypt(EncryptAnnotationInfoBean infoBean, EncryptBodyConfig config,
					String formatStringBody) {
				String key = CommonUtils.checkAndGetKey(config.getAesKey(), infoBean.getKey(), "AES-KEY");
				return AESUtil.dataEncrypt(formatStringBody, key);
			}

		};
	}

	/**
	 * rsa加密
	 * 
	 * @return
	 */
	public static EncryptHandler rsaEncryptHandler() {
		return new EncryptHandler() {
			@Override
			public String encrypt(EncryptAnnotationInfoBean infoBean, EncryptBodyConfig config,
					String formatStringBody) {
				RSA rsa = CommonUtils.infoBeanToRsaInstance(infoBean);
				return rsa.encryptHex(formatStringBody, infoBean.getRsaKeyType().toolType);
			}

		};
	}

	/**
	 * sm加密
	 * 
	 * @return
	 */
	static EncryptHandler smEncryptHandler() {
		return new EncryptHandler() {
			@Override
			public String encrypt(EncryptAnnotationInfoBean infoBean, EncryptBodyConfig config,
					String formatStringBody) {
				String key = CommonUtils.checkAndGetKey(config.getSmKey(), infoBean.getKey(), "SM-KEY");
				return SM4Util.encrypt(formatStringBody, key);
			}

		};
	}

}
