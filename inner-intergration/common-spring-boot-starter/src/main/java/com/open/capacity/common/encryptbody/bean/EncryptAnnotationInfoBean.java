package com.open.capacity.common.encryptbody.bean;

import com.open.capacity.common.encryptbody.enums.EncryptBodyMethod;
import com.open.capacity.common.encryptbody.enums.RSAKeyType;
import com.open.capacity.common.encryptbody.enums.SHAEncryptType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>加密注解信息</p>
 *
 * @author licoy.cn
 * @version 2018/9/6
 * code:https://gitee.com/licoy/encrypt-body-spring-boot-starter
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EncryptAnnotationInfoBean implements ISecurityInfo {

    private EncryptBodyMethod encryptBodyMethod;

    private SHAEncryptType shaEncryptType;

    private String key;

    private RSAKeyType rsaKeyType;

}
