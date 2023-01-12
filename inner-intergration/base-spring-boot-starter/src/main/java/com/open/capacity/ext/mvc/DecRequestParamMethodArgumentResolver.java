/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.mvc;

import com.open.capacity.ext.mvc.context.DecRequestContext;
import com.open.capacity.ext.mvc.context.RequestContextUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.RequestParamMethodArgumentResolver;

public class DecRequestParamMethodArgumentResolver extends RequestParamMethodArgumentResolver {
    public DecRequestParamMethodArgumentResolver(boolean useDefaultResolution) {
        super(useDefaultResolution);
    }

    public DecRequestParamMethodArgumentResolver(ConfigurableBeanFactory beanFactory, boolean useDefaultResolution) {
        super(beanFactory, useDefaultResolution);
    }

    @Override
    protected Object resolveName(String name, MethodParameter parameter, NativeWebRequest request) throws Exception {
        DecRequestContext context = RequestContextUtils.getReqContext();
        if (context.isAllQueryDes() || (context.isQueryReset() && context.hasResetQuery(name))){
            return context.getResetQuery(name);
        }
        return super.resolveName(name, parameter, request);
    }
}
