/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.exception;

public class HsExtRuntimeException extends RuntimeException{
    public HsExtRuntimeException() {
    }

    public HsExtRuntimeException(String message) {
        super(message);
    }

    public HsExtRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public HsExtRuntimeException(Throwable cause) {
        super(cause);
    }

    public HsExtRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
