package com.open.capacity.common.encryptbody.annotation.encrypt;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.open.capacity.common.encryptbody.enums.EncryptBodyMethod;
import com.open.capacity.common.encryptbody.enums.SHAEncryptType;

/**
 * <p>加密{@link org.springframework.web.bind.annotation.ResponseBody}响应数据，可用于整个控制类或者某个控制器上</p>
 * @author licoy.cn
 * @version 2018/9/4
 * code:https://gitee.com/licoy/encrypt-body-spring-boot-starter
 */
@Target(value = {ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EncryptBody {

    EncryptBodyMethod value() default EncryptBodyMethod.MD5;

    String otherKey() default "";

    SHAEncryptType shaType() default SHAEncryptType.SHA256;

}
