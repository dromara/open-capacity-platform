package com.open.capacity.common.encryptbody.util;


import com.open.capacity.common.encryptbody.enums.SHAEncryptType;
import com.open.capacity.common.encryptbody.exception.DecryptMethodNotFoundException;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.Digester;

/**
 * <p>SHA加密工具类</p>
 *
 * @author licoy.cn
 * @version 2018/9/5
 * code:https://gitee.com/licoy/encrypt-body-spring-boot-starter
 */
public class ShaEncryptUtil {

    /**
     * SHA加密公共方法
     *
     * @param str  目标字符串
     * @param type 加密类型 {@link SHAEncryptType}
     */
    public static String encrypt(String str, SHAEncryptType type) {
        Digester digester;
        switch (type) {
            case SHA1:
                digester = SecureUtil.sha1();
                break;
            case SHA256:
                digester = SecureUtil.sha256();
                break;
            default:
                throw new DecryptMethodNotFoundException();
        }
        return String.valueOf(digester.digestHex(str));
    }
}
