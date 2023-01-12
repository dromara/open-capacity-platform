/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.mvc.decrypt;

import com.open.capacity.ext.mvc.exception.RequestDesInvokerException;
import org.springframework.core.MethodParameter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RequestDecStrategy {
    /**
     * 策略处理bean
     */
    private Object strategyBean;
    /**
     * 策略处理具体方法
     */
    private Method strategyMethod;
    /**
     * 策略处理方法请求参数类型
     */
    private MethodParameter[] parameters;

    public RequestDecStrategy(Object strategyBean, Method strategyMethod, MethodParameter[] parameters) {
        this.strategyBean = strategyBean;
        this.strategyMethod = strategyMethod;
        this.parameters = parameters;
    }

    public MethodParameter[] getParameters() {
        return parameters;
    }

    public Object invokeStrategy(Object[] args) throws RequestDesInvokerException {
        try {
            return strategyMethod.invoke(strategyBean,args);
        }catch  (InvocationTargetException | IllegalAccessException e) {
            throw new RequestDesInvokerException("request解密失败",e);
        }
    }
}
