package com.open.capacity.common.annotation;

import java.lang.annotation.*;

/**
 * 当前登录人注解
 * @author zlt
 * @date 2018/7/24 16:44
 * only deal head access_token  
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LoginUser {
    /**
     * 是否查询SysUser对象所有信息，true则通过rpc接口查询
     */
    boolean isFull() default false;
}
