/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.dispatcher;

import com.open.capacity.ext.dispatcher.exception.DispatchInvokerException;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * TODO Description
 *
 * @author: hillchen
 * @data: 2023-02-17 13:43
 */
public class DsMethodHandler {
    private Object invokerBean;
    private Type[] paramClass;
    private Method invokerMethod;
    private String groupName;
    private String bizName;

    public DsMethodHandler(Object invokerBean, Type[] paramClass, Method invokerMethod, String groupName, String bizName) {
        this.invokerBean = invokerBean;
        this.paramClass = paramClass;
        this.invokerMethod = invokerMethod;
        this.groupName = groupName;
        this.bizName = bizName;
    }

    public Object invoke(Object[] args) throws DispatchInvokerException {
        try {
            return invokerMethod.invoke(invokerBean,args);
        }catch (Exception e){
            throw new DispatchInvokerException(String.format("执行分发处理[groupName:%s,bizName:%s]方式失败",groupName,bizName),e);
        }
    }

    public Type[] getParamClass() {
        return paramClass;
    }
}
