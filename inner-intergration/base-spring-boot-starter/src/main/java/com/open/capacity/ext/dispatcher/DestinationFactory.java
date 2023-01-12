/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.dispatcher;

import com.open.capacity.ext.dispatcher.exception.DispatchRegisterException;
import com.open.capacity.ext.dispatcher.exception.DispatchRouteNoFoundException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 分发目标处理器工厂
 */
public class DestinationFactory {
    private DestinationFactory(){};
    private static Map<DispatchRoute, DsMethodHandler> HANDLER_MAP = new ConcurrentHashMap<>();

    static void registerStrategy(DispatchRoute dispatchRoute, DsMethodHandler handler){
        if (HANDLER_MAP.containsKey(dispatchRoute)){
            throw new DispatchRegisterException(String.format("[group:%s,biz:%s]处理器注册，不能重复注册",dispatchRoute.getGroupName(),dispatchRoute.getBizName()));
        }
        HANDLER_MAP.putIfAbsent(dispatchRoute, handler);
    }
    public static DsMethodHandler selectHandler(DispatchRoute dispatchRoute){
        if (!HANDLER_MAP.containsKey(dispatchRoute)){
            throw new DispatchRouteNoFoundException(String.format("[group:%s,biz:%s]处理器未找到",dispatchRoute.getGroupName(),dispatchRoute.getBizName()));
        }
        return HANDLER_MAP.get(dispatchRoute);
    }
}
