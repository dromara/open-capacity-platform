package com.open.capacity.common.encryptbody.bean;

import com.open.capacity.common.encryptbody.enums.DecryptBodyMethod;
import com.open.capacity.common.encryptbody.enums.RSAKeyType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>解密注解信息</p>
 * @author licoy.cn
 * @version 2018/9/6
 * code:https://gitee.com/licoy/encrypt-body-spring-boot-starter
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DecryptAnnotationInfoBean implements ISecurityInfo {

    private DecryptBodyMethod decryptBodyMethod;

    private String key;

    private RSAKeyType rsaKeyType;

}
