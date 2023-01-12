package com.open.capacity.common.encryptbody.enums;

import com.open.capacity.common.encryptbody.handler.EncryptHandler;

import lombok.Getter;

/**
 * <p>
 * 枚举加密方式策略
 * </p>
 * 
 * @author licoy.cn
 * @version 2018/9/4
  * code:https://gitee.com/licoy/encrypt-body-spring-boot-starter
 */
@Getter
public enum EncryptBodyMethod {

	/**
	 * MD5
	 */
	MD5(EncryptHandler.md5EncryptHandler()),
	/**
	 * SHA
	 */
	SHA(EncryptHandler.shaEncryptHandler()),
	/**
	 * DES
	 */
	DES(EncryptHandler.desEncryptHandler()),
	/**
	 * AES
	 */
	AES(EncryptHandler.aesEncryptHandler()),
	/**
	 * RSA
	 */
	RSA(EncryptHandler.rsaEncryptHandler()), 
	/**
	 * SM
	 */
	SM(EncryptHandler.smEncryptHandler());

	EncryptBodyMethod(EncryptHandler encryptHandler) {
		this.encryptHandler = encryptHandler;
	}

	/**
	 * 加密接口
	 */
	EncryptHandler encryptHandler;

	

}
