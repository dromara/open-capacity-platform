package com.open.capacity.common.encryptbody.handler;

import com.open.capacity.common.algorithm.AESUtil;
import com.open.capacity.common.algorithm.SM4Util;
import com.open.capacity.common.encryptbody.bean.DecryptAnnotationInfoBean;
import com.open.capacity.common.encryptbody.config.EncryptBodyConfig;
import com.open.capacity.common.encryptbody.util.CommonUtils;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.RSA;

/**
 * 解密接口
 * 
 * @author licoy.cn
 * @version 2022/3/29
 *          code:https://gitee.com/licoy/encrypt-body-spring-boot-starter
 */
public interface DecryptHandler {

	default String decrypt(DecryptAnnotationInfoBean infoBean, EncryptBodyConfig config, String formatStringBody) {
		return null;
	}

	/**
	 * 默认实现
	 * 
	 * @return
	 */
	public static DecryptHandler decryptHandler() {
		return new DecryptHandler() {
		};
	}

	/**
	 * des解密
	 * 
	 * @return
	 */
	public static DecryptHandler desDecryptHandler() {
		return new DecryptHandler() {
			@Override
			public String decrypt(DecryptAnnotationInfoBean infoBean, EncryptBodyConfig config,
					String formatStringBody) {
				String key = CommonUtils.checkAndGetKey(config.getDesKey(), infoBean.getKey(), "DES-KEY");
				return SecureUtil.des(key.getBytes()).decryptStr(formatStringBody);
			}
		};
	}

	/**
	 * aes解密
	 * 
	 * @return
	 */
	public static DecryptHandler aesDecryptHandler() {
		return new DecryptHandler() {
			@Override
			public String decrypt(DecryptAnnotationInfoBean infoBean, EncryptBodyConfig config,
					String formatStringBody) {
				String key = CommonUtils.checkAndGetKey(config.getAesKey(), infoBean.getKey(), "AES-KEY");
				return AESUtil.dataDecrypt(formatStringBody, key);
			}

		};
	}

	/**
	 * rsa解密
	 * 
	 * @return
	 */
	public static DecryptHandler rsaDecryptHandler() {
		return new DecryptHandler() {
			@Override
			public String decrypt(DecryptAnnotationInfoBean infoBean, EncryptBodyConfig config,
					String formatStringBody) {
				RSA rsa = CommonUtils.infoBeanToRsaInstance(infoBean);
				return rsa.decryptStr(formatStringBody, infoBean.getRsaKeyType().toolType);

			}

		};
	}

	/**
	 * sm解密
	 * 
	 * @return
	 */
	public static DecryptHandler smDecryptHandler() {
		return new DecryptHandler() {
			@Override
			public String decrypt(DecryptAnnotationInfoBean infoBean, EncryptBodyConfig config,
					String formatStringBody) {
				String key = CommonUtils.checkAndGetKey(config.getSmKey(), infoBean.getKey(), "SM-KEY");
				return SM4Util.decrypt(formatStringBody, key);

			}

		};
	}
}
