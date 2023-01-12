package com.open.capacity.gateway.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.open.capacity.gateway.config.DynamicRouteConfig;

/**
 * @author someday
 * @date 2018/7/26
 * 网关动态路由
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(DynamicRouteConfig.class)
public @interface EnableNacosDynamicRoute {
}
