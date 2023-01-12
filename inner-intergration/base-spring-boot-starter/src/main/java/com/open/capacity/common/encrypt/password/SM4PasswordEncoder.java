package com.open.capacity.common.encrypt.password;


import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.open.capacity.common.algorithm.SM3Util;
import com.open.capacity.common.algorithm.SM4Util;
import com.open.capacity.common.exception.BusinessException;

import lombok.extern.slf4j.Slf4j;

/**
 * 数据库SM3 + SM4 hash 算法
 */
@Slf4j
public class SM4PasswordEncoder implements PasswordEncoder {

	@Override
	public String encode(CharSequence rawPassword) {
		String password = rawPassword.toString();
		String sm3Code = ByteUtils.toHexString(SM3Util.hash(password.getBytes()));
		String sm4Pass = "";
		try {
			sm4Pass = ByteUtils.toHexString(SM4Util.encryptCbcPadding(ByteUtils.fromHexString(sm3Code.substring(0, 32)), ByteUtils.fromHexString(sm3Code.substring(32, 64)), password.getBytes()));
		} catch (Exception e) {
			throw new BusinessException("密码加密失败!");
		}
		return sm3Code + "|" + sm4Pass;
	}

	/**
	 * Verify the encoded password obtained from storage matches the submitted raw
	 * password after it too is encoded. Returns true if the passwords match, false if
	 * they do not. The stored password itself is never decoded.
	 *
	 * @param rawPassword     the raw password to encode and match
	 * @param encodedPassword the encoded password from storage to compare with
	 * @return true if the raw password, after encoding, matches the encoded password from
	 * storage
	 */
	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		String[] encryptAllDbPassArr = encodedPassword.split("\\|");
		if (encryptAllDbPassArr.length != 2) {
			throw new BusinessException("用户存储密码已被篡改!");
		}

		String encryptDbKey = encryptAllDbPassArr[0];
		String encryptDbPass = encryptAllDbPassArr[1];

		// 密码存储防篡改校验
		try {
			byte[] decryptDbPassCode = SM4Util.decryptCbcPadding(ByteUtils.fromHexString(encryptDbKey.substring(0, 32)), ByteUtils.fromHexString(encryptDbKey.substring(32, 64)), ByteUtils.fromHexString(encryptDbPass));
			boolean matchDbFlag = SM3Util.verify(decryptDbPassCode, ByteUtils.fromHexString(encryptDbKey));
			if (!matchDbFlag) {
				log.error("密码可能发生了篡改！");
				throw new BusinessException("用户存储密码已被篡改!");
			}
			if (rawPassword.equals(new String(decryptDbPassCode))) {
				return true;
			}

		} catch (Exception e) {
			log.error("密码解密失败，{}",e);
			throw new BusinessException("用户存储密码已被篡改!");
		}

		return false;
	}
}
