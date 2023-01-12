package com.open.capacity.common.encryptbody.enums;

import java.io.Serializable;

import cn.hutool.crypto.asymmetric.KeyType;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * RSA的密钥类型
 *
 * @author Licoy
 * @date 2022/3/28
 * code:https://gitee.com/licoy/encrypt-body-spring-boot-starter
 */
@AllArgsConstructor
@Getter
public enum RSAKeyType implements Serializable {

    /**
     * 公钥
     */
    PUBLIC(1, KeyType.PublicKey),

    /**
     * 私钥
     */
    PRIVATE(2, KeyType.PrivateKey);

    public final int type;

    public final KeyType toolType;

    }
