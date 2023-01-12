/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.mvc.encryption;

import com.open.capacity.ext.mvc.exception.RequestDesInvokerException;
import org.springframework.core.MethodParameter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ResponseEncStrategy {
    /**
     * 策略处理bean
     */
    private Object strategyBean;
    /**
     * 策略处理具体方法
     */
    private Method strategyMethod;

    public ResponseEncStrategy(Object strategyBean, Method strategyMethod) {
        this.strategyBean = strategyBean;
        this.strategyMethod = strategyMethod;
    }
    public Object invokeStrategy(Object result) throws RequestDesInvokerException {
        try {
            return strategyMethod.invoke(strategyBean,result);
        }catch  (InvocationTargetException | IllegalAccessException e) {
            throw new RequestDesInvokerException("之前request解密失败",e);
        }
    }
}
