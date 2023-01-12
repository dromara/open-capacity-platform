/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.mvc;

import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

public class WarpMethodReturnValueHandler implements HandlerMethodReturnValueHandler {
    private final HandlerMethodReturnValueHandler targetHandler ;

    public WarpMethodReturnValueHandler(HandlerMethodReturnValueHandler targetHandler) {
        this.targetHandler = targetHandler;
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return targetHandler.supportsReturnType(returnType);
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        targetHandler.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
    }

    public static HandlerMethodReturnValueHandler warp(HandlerMethodReturnValueHandler targetHandler){
        return new WarpMethodReturnValueHandler(targetHandler);
    }
}
