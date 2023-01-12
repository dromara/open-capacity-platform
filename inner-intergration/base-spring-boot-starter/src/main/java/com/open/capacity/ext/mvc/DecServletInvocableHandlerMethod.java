/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.mvc;

import com.open.capacity.ext.commom.utli.ReflectUtils;
import com.open.capacity.ext.mvc.context.DecRequestContext;
import com.open.capacity.ext.mvc.context.RequestContextUtils;
import com.open.capacity.ext.mvc.decrypt.RequestDec;
import com.open.capacity.ext.mvc.decrypt.RequestDecStrategy;
import com.open.capacity.ext.mvc.encryption.ResponseEnc;
import com.open.capacity.ext.mvc.encryption.ResponseEncStrategy;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.HandlerMethodArgumentResolverComposite;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;

import java.lang.reflect.Method;
import java.util.Objects;

public class DecServletInvocableHandlerMethod extends ServletInvocableHandlerMethod {
    private static final Object[] EMPTY_ARGS = new Object[0];
    public DecServletInvocableHandlerMethod(Object handler, Method method) {
        super(handler, method);
    }

    public DecServletInvocableHandlerMethod(HandlerMethod handlerMethod) {
        super(handlerMethod);
    }

    @Override
    protected Object doInvoke(Object... args) throws Exception {
        DecRequestContext context = RequestContextUtils.getReqContext();
        Object reqResult =  super.doInvoke(args);
        context.setReqResult(reqResult);

        // controller 处理器method
        Method method = getMethod();
        // 获取响应结果加密策略参数
        ResponseEnc responseEnc = AnnotationUtils.findAnnotation(method, ResponseEnc.class);
        if (Objects.nonNull(responseEnc) && StringUtils.hasText(responseEnc.encType())){
            ResponseEncStrategy responseEncStrategy = ResponseEncStrategyFactory.selectStrategy(responseEnc.encType());
            if (Objects.nonNull(responseEncStrategy)){
                Object encResult =  responseEncStrategy.invokeStrategy(reqResult);
                context.setEncResult(encResult);
                return encResult;
            }
        }
        return reqResult;
    }

    @Override
    protected Object[] getMethodArgumentValues(NativeWebRequest request, ModelAndViewContainer mavContainer, Object... providedArgs) throws Exception {
        DecRequestContext context = RequestContextUtils.getReqContext();
        // controller 处理器method
        Method method = getMethod();
        // 获取请求参数解密策略参数
        RequestDec requestDec = AnnotationUtils.findAnnotation(method, RequestDec.class);
        if (Objects.nonNull(requestDec) && StringUtils.hasText(requestDec.decType())){
            RequestDecStrategy requestDecStrategy = RequestDecStrategyFactory.selectStrategy(requestDec.decType());
            Object[] decArgs = innerGetMethodArgumentValues(requestDecStrategy.getParameters(),request,mavContainer,providedArgs);
            context.setDesArgs(decArgs);
            requestDecStrategy.invokeStrategy(decArgs);
        }
        Object[] reqArgs = innerGetMethodArgumentValues(getMethodParameters(),request, mavContainer, providedArgs);
        context.setReqArgs(reqArgs);
        return reqArgs;
    }

    protected Object[] innerGetMethodArgumentValues(MethodParameter[] parameters,NativeWebRequest request, @Nullable ModelAndViewContainer mavContainer,
                                               Object... providedArgs) throws Exception {
        if (ObjectUtils.isEmpty(parameters)) {
            return EMPTY_ARGS;
        }
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            MethodParameter parameter = parameters[i];
            args[i] = innerGetMethodArgumentValue(parameter,request,mavContainer,providedArgs);
        }
        return args;
    }
    private Object innerGetMethodArgumentValue(MethodParameter parameter,NativeWebRequest request, @Nullable ModelAndViewContainer mavContainer,
                                                Object... providedArgs) throws Exception{
        Object org;
        parameter.initParameterNameDiscovery(getParameterNameDiscoverer());
        org = findProvidedArgument(parameter, providedArgs);
        if (org != null) {
            return org;
        }
        HandlerMethodArgumentResolverComposite resolvers = getResolvers();
        if (!resolvers.supportsParameter(parameter)) {
            throw new IllegalStateException(formatArgumentError(parameter, "No suitable resolver"));
        }
        try {
            return resolvers.resolveArgument(parameter, mavContainer, request, getDataBinderFactory());
        } catch (Exception ex) {
            // Leave stack trace for later, exception may actually be resolved and handled...
            if (logger.isDebugEnabled()) {
                String exMsg = ex.getMessage();
                if (exMsg != null && !exMsg.contains(parameter.getExecutable().toGenericString())) {
                    logger.debug(formatArgumentError(parameter, exMsg));
                }
            }
            throw ex;
        }
    }
    private ParameterNameDiscoverer getParameterNameDiscoverer(){
        return (ParameterNameDiscoverer)ReflectUtils.getBeanFieldVal(this,"parameterNameDiscoverer");
    }

    private HandlerMethodArgumentResolverComposite getResolvers(){
        return (HandlerMethodArgumentResolverComposite)ReflectUtils.getBeanFieldVal(this,"resolvers");
    }

    private WebDataBinderFactory getDataBinderFactory(){
        return (WebDataBinderFactory)ReflectUtils.getBeanFieldVal(this,"dataBinderFactory");
    }
}
