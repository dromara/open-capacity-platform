/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.mvc.exception;

import com.open.capacity.ext.exception.HsExtRuntimeException;

public class RequestDesInvokerException extends HsExtRuntimeException {
    public RequestDesInvokerException() {
    }

    public RequestDesInvokerException(String message) {
        super(message);
    }

    public RequestDesInvokerException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestDesInvokerException(Throwable cause) {
        super(cause);
    }

    public RequestDesInvokerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
