/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.mvc;

import com.open.capacity.ext.mvc.decrypt.RequestDecStrategy;
import com.open.capacity.ext.mvc.exception.RequestDesRegisterException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class RequestDecStrategyFactory {
    private RequestDecStrategyFactory(){};
    private static Map<String, RequestDecStrategy> STRATEGY_MAP = new ConcurrentHashMap<>();

    static void registerStrategy(String strategyType, RequestDecStrategy strategy){
        if (STRATEGY_MAP.containsKey(strategyType)){
            throw new RequestDesRegisterException(String.format("%s解析策略以及注册,不能重复注册",strategyType));
        }
        STRATEGY_MAP.putIfAbsent(strategyType, strategy);
    }
    static RequestDecStrategy selectStrategy(String strategyType){
        return STRATEGY_MAP.get(strategyType);
    }
}
