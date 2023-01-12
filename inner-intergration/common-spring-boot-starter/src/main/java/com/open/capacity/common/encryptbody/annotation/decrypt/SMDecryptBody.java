package com.open.capacity.common.encryptbody.annotation.decrypt;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.open.capacity.common.encryptbody.annotation.encrypt.EncryptBody;

/**
 * @author licoy.cn
 * @version 2018/9/4
 * @see EncryptBody
 * code:https://gitee.com/licoy/encrypt-body-spring-boot-starter
 */
@Target(value = {ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SMDecryptBody {

	String key() default "";

}
