/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.mvc;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class WarpMethodArgumentResolver implements HandlerMethodArgumentResolver {
    private final HandlerMethodArgumentResolver targetResolver ;

    private WarpMethodArgumentResolver(HandlerMethodArgumentResolver targetResolver) {
        this.targetResolver = targetResolver;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return targetResolver.supportsParameter(parameter);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Object arg = targetResolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
        return arg;
    }

    public static HandlerMethodArgumentResolver warp(HandlerMethodArgumentResolver handlerMethodArgumentResolver){
        return new WarpMethodArgumentResolver(handlerMethodArgumentResolver);
    }
}
