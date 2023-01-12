/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.mvc;

import com.open.capacity.ext.commom.utli.ReflectUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolverComposite;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.HandlerMethodReturnValueHandlerComposite;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;

import java.util.*;
import java.util.function.Function;

public class DecRequestMappingHandlerAdapter extends RequestMappingHandlerAdapter {

    @Override
    protected ServletInvocableHandlerMethod createInvocableHandlerMethod(HandlerMethod handlerMethod) {
        return new DecServletInvocableHandlerMethod(handlerMethod);
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        // 重置请求参数转换器顺序
        resortAndWarpArgumentResolvers();
        // 重置响应结果转换器顺序
        resortAndWarpMethodReturnValueHandler();
    }

    private void resortAndWarpArgumentResolvers() {
        List<HandlerMethodArgumentResolver> resolvers = getHandlerMethodArgumentResolvers(getHandlerMethodArgumentResolverComposite());
        resort(resolvers,getCustomArgumentResolvers(),WarpMethodArgumentResolver::warp);
        resolvers.add(0,new DecRequestParamMethodArgumentResolver(getBeanFactory(),false));
        resolvers.add(0,new DecRequestResponseBodyMethodProcessor(getMessageConverters(),getRequestResponseBodyAdvice()));
    }
    private void resortAndWarpMethodReturnValueHandler() {
        List<HandlerMethodReturnValueHandler> handlers = getHandlerMethodReturnValueHandlers(getHandlerMethodReturnValueHandlerComposite());
        resort(getHandlerMethodReturnValueHandlers(getHandlerMethodReturnValueHandlerComposite()),getCustomReturnValueHandlers(),WarpMethodReturnValueHandler::warp);
        handlers.add(0,new DecRequestResponseBodyMethodProcessor(getMessageConverters(),getContentNegotiationManager(),getRequestResponseBodyAdvice()));
    }

    private <T> void  resort(List<T> allEntities,List<T>harderEntities,Function<T,T> warpFun) {
        Set<T> harderSet = CollectionUtils.isEmpty(harderEntities) ? new HashSet<>() : new HashSet<>(harderEntities);

        List<T> warpAll = new ArrayList<>();
        List<T> warpHarder = new ArrayList<>();

        Iterator<T> entityIterator = allEntities.iterator();
        if (entityIterator.hasNext()){
            T entity = entityIterator.next();
            T warp = warpFun.apply(entity);
            entityIterator.remove();
            if (harderSet.contains(entity)){
                warpHarder.add(warp);
            }else {
                warpAll.add(warp);
            }
        }
        if (!CollectionUtils.isEmpty(warpHarder)){
            warpAll.addAll(0,warpHarder);
        }
        allEntities.addAll(0,harderEntities);
    }


    private HandlerMethodReturnValueHandlerComposite getHandlerMethodReturnValueHandlerComposite() {
        return  (HandlerMethodReturnValueHandlerComposite)ReflectUtils.getBeanFieldVal(this,"returnValueHandlers");
    }


    private List<HandlerMethodReturnValueHandler> getHandlerMethodReturnValueHandlers(HandlerMethodReturnValueHandlerComposite returnValueHandlerComposite) {
        return (List<HandlerMethodReturnValueHandler>)ReflectUtils.getBeanFieldVal(returnValueHandlerComposite,"returnValueHandlers");
    }

    private HandlerMethodArgumentResolverComposite getHandlerMethodArgumentResolverComposite() {
        return  (HandlerMethodArgumentResolverComposite)ReflectUtils.getBeanFieldVal(this,"argumentResolvers");
    }

    private List<HandlerMethodArgumentResolver> getHandlerMethodArgumentResolvers(HandlerMethodArgumentResolverComposite argumentResolverComposite) {
        return (LinkedList<HandlerMethodArgumentResolver>)ReflectUtils.getBeanFieldVal(argumentResolverComposite,"argumentResolvers");
    }
    private List<Object> getRequestResponseBodyAdvice (){
        return (List<Object>) ReflectUtils.getBeanFieldVal(this,"requestResponseBodyAdvice");
    }

    private ContentNegotiationManager getContentNegotiationManager (){
        return (ContentNegotiationManager) ReflectUtils.getBeanFieldVal(this,"contentNegotiationManager");
    }


}
