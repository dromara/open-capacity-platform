package com.open.capacity.common.encryptbody;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.open.capacity.common.encryptbody.advice.DecryptRequestBodyAdvice;
import com.open.capacity.common.encryptbody.advice.EncryptResponseBodyAdvice;
import com.open.capacity.common.encryptbody.config.EncryptBodyConfig;

/**
 * 加入自动扫描
 * @author licoy.cn
 * @version 2018/9/5
 * code:https://gitee.com/licoy/encrypt-body-spring-boot-starter
 */
@Configuration
@Import({ EncryptBodyConfig.class, EncryptResponseBodyAdvice.class, DecryptRequestBodyAdvice.class, })
@ConditionalOnWebApplication
public class EncryptAutoConfiguration  {
}
