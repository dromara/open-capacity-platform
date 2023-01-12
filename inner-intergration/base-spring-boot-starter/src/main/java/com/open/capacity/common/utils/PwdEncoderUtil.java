package com.open.capacity.common.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.util.Assert;

import com.open.capacity.common.encrypt.password.SM3PasswordEncoder;
import com.open.capacity.common.encrypt.password.SM4PasswordEncoder;

import lombok.experimental.UtilityClass;

/**
 * PasswordEncoder实现工具类
 *
 * @author someday
 * @version 1.0
 * @date 2018/5/7 code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@UtilityClass
public class PwdEncoderUtil {
	public PasswordEncoder getDelegatingPasswordEncoder(String encodingId) {
		Map<String, PasswordEncoder> encoders = new HashMap<>();
		
		encoders.put("sm3", new SM3PasswordEncoder());
		encoders.put("sm4", new SM4PasswordEncoder());
		encoders.put("bcrypt", new BCryptPasswordEncoder(4));
		encoders.put("ldap", new org.springframework.security.crypto.password.LdapShaPasswordEncoder());
		encoders.put("MD4", new org.springframework.security.crypto.password.Md4PasswordEncoder());
		encoders.put("MD5", new org.springframework.security.crypto.password.MessageDigestPasswordEncoder("MD5"));
		// 在 spring security 5 虽然被弃用了，但是要兼容clientSecret,所以得加
		encoders.put("noop", org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance());
		encoders.put("pbkdf2", new Pbkdf2PasswordEncoder());
		encoders.put("scrypt", new SCryptPasswordEncoder());
		encoders.put("SHA-1", new org.springframework.security.crypto.password.MessageDigestPasswordEncoder("SHA-1"));
		encoders.put("SHA-256", new org.springframework.security.crypto.password.MessageDigestPasswordEncoder("SHA-256"));
		encoders.put("sha256", new org.springframework.security.crypto.password.StandardPasswordEncoder());
		encoders.put("argon2", new Argon2PasswordEncoder());

		Assert.isTrue(encoders.containsKey(encodingId), encodingId + " is not found in idToPasswordEncoder");

		DelegatingPasswordEncoder delegatingPasswordEncoder = new DelegatingPasswordEncoder(encodingId, encoders);
		delegatingPasswordEncoder.setDefaultPasswordEncoderForMatches(encoders.get(encodingId));
		return delegatingPasswordEncoder;
	}
}
