/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.mvc.exception;

import com.open.capacity.ext.exception.HsExtRuntimeException;

public class RequestDesRegisterException extends HsExtRuntimeException {
    public RequestDesRegisterException() {
    }

    public RequestDesRegisterException(String message) {
        super(message);
    }

    public RequestDesRegisterException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestDesRegisterException(Throwable cause) {
        super(cause);
    }

    public RequestDesRegisterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
