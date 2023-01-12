package com.open.capacity.common.idempotent.annotation;

import java.lang.annotation.*;

/**
 * 自定义防重提交
 * @author someday
 * @date 2018/9/25
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RepeatSubmit {


    /**
     * 防重提交，支持两种，一个是方法参数，一个是令牌
     */
    enum Type { PARAM, TOKEN }

    /**
     * 默认防重提交，是方法参数
     * @return
     */
    Type limitType() default Type.PARAM;


    /**
     * 加锁过期时间，默认是5秒
     * @return
     */
    long lockTime() default 5;
    
    String serviceId() default ""; 
    
}
