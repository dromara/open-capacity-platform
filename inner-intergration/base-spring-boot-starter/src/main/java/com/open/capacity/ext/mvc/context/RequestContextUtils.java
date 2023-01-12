/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.mvc.context;

import java.util.Objects;

public class RequestContextUtils {
    private static ThreadLocal<DecRequestContext> REQ_CONTEXT = new ThreadLocal<>();
    public static DecRequestContext getReqContext(){
        DecRequestContext context = REQ_CONTEXT.get();
        if (Objects.isNull(context)){
            context  = new DecRequestContext();
            REQ_CONTEXT.set(context);
        }
        return context;
    }
    public static void cleanContext(){
        REQ_CONTEXT.remove();
    }
    public static void resetReqBody(String body){
        DecRequestContext context = getReqContext();
        context.getMidVal().put(ReqParamKey.buildReqBodyKey(),body);
        context.setBodyReset(true);
    }
    public static void resetReqQuery(String name,String value){
        DecRequestContext context = getReqContext();
        context.getMidVal().put(ReqParamKey.buildReqQueryKey(name),value);
        context.setQueryReset(true);
    }
    public static String getResetBody(){
        DecRequestContext context = getReqContext();
        return context.getMidVal().get(ReqParamKey.buildReqBodyKey());
    }
    public static String getResetQuery(String name){
        DecRequestContext context = getReqContext();
        return context.getResetQuery(name);
    }
}
