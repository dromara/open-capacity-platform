package com.open.capacity.common.encryptbody.annotation.encrypt;


import java.lang.annotation.*;

/**
 * @author licoy.cn
 * @version 2018/9/4
 * @see EncryptBody
 * code:https://gitee.com/licoy/encrypt-body-spring-boot-starter
 */
@Target(value = {ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SMEncryptBody {

	String key() default "";

}

