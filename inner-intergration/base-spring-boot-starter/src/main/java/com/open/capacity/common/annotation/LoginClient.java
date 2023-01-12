package com.open.capacity.common.annotation;

import java.lang.annotation.*;

/**
 * 客户端主键
 * @author zlt
 * @date 2018/7/24 16:44
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LoginClient {
}
