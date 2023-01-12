/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.dispatcher;

import com.open.capacity.ext.dispatcher.exception.DispatchInvokerException;

/**
 * TODO Description
 *
 * @author: hillchen
 * @data: 2023-02-17 14:46
 */
public interface Dispatcher<R,C extends DispatcherContext> {
    default Object dispatch(R requestData) throws DispatchInvokerException {
        // 将请求参数解析出context
        C context = resolveContext(requestData);
        // 获取路由标示
        DispatchRoute route = route(requestData,context);
        // 解析出请求参数
        Object[] args = resolveArgs(requestData,context);
        // 请求分发
        DsMethodHandler dsMethodHandler = DestinationFactory.selectHandler(route);
        return dsMethodHandler.invoke(args);
    }

    DispatchRoute route(R requestData,C context);

    C resolveContext(R requestData);

    Object[] resolveArgs(R requestData,C context);
}
