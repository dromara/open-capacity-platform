/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.exception;

public class HsExtException extends Exception{
    public HsExtException() {
    }

    public HsExtException(String message) {
        super(message);
    }

    public HsExtException(String message, Throwable cause) {
        super(message, cause);
    }

    public HsExtException(Throwable cause) {
        super(cause);
    }

    public HsExtException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
