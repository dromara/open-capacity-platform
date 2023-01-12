package com.open.capacity.common.encryptbody.enums;

import com.open.capacity.common.encryptbody.handler.DecryptHandler;

import lombok.Getter;

/**
 * <p>
 * 枚举解密方式策略
 * </p>
 * 
 * @author licoy.cn
 * @version 2018/9/4
 * code:https://gitee.com/licoy/encrypt-body-spring-boot-starter
 */
@Getter
public enum DecryptBodyMethod {

	/**
	 * DES
	 */
	DES(DecryptHandler.desDecryptHandler()),
	/**
	 * AES
	 */
	AES(DecryptHandler.aesDecryptHandler()),
	/**
	 * RAS
	 */
	RSA(DecryptHandler.rsaDecryptHandler()), 
	
	/**
	 * SM
	 */
	SM(DecryptHandler.smDecryptHandler());
	
	DecryptBodyMethod(DecryptHandler decryptHandler) {
		this.decryptHandler = decryptHandler;
	}
	/**
	 * 解密接口
	 * @param decryptHandler
	 */
	DecryptHandler decryptHandler;
	
}
