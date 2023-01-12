package com.open.capacity.common.encrypt.password;


import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.open.capacity.common.algorithm.SM3Util;
import com.open.capacity.common.algorithm.SM4Util;
import com.open.capacity.common.exception.BusinessException;
import com.open.capacity.common.utils.PwdEncoderUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 数据库SM3 算法
 */
@Slf4j
public class SM3PasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence rawPassword) {
    	
    	String password = rawPassword.toString();
		String sm3Code = ByteUtils.toHexString(SM3Util.hash(password.getBytes()));
        return sm3Code ;
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (rawPassword == null) {
            throw new IllegalArgumentException("rawPassword cannot be null");
        }
        if (encodedPassword == null || encodedPassword.length() == 0) {
            log.warn("Empty encoded password");
            return false;
        }
        String rawPasswordEncoded = this.encode(rawPassword.toString());
        return rawPasswordEncoded.equals(encodedPassword);
    }
     
}