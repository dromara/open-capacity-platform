package com.open.capacity.common.encryptbody.bean;

import java.io.Serializable;

import com.open.capacity.common.encryptbody.enums.RSAKeyType;

/**
 * 安全信息实体
 *
 * @author Licoy
 * @date 2022/3/29
 * code:https://gitee.com/licoy/encrypt-body-spring-boot-starter
 */
public interface ISecurityInfo extends Serializable {

    /**
     * 获取key
     *
     * @return key
     */
    String getKey();

    /**
     * 获取rsa的密钥类型
     *
     * @return 密钥类型
     */
    RSAKeyType getRsaKeyType();

}
