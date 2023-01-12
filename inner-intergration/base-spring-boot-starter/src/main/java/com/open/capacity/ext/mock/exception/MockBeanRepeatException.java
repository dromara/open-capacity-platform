/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.mock.exception;

public class MockBeanRepeatException extends RuntimeException {
    public MockBeanRepeatException(String message) {
        super(message);
    }

    public MockBeanRepeatException(String message, Throwable cause) {
        super(message, cause);
    }

    public MockBeanRepeatException(Throwable cause) {
        super(cause);
    }

    public MockBeanRepeatException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public MockBeanRepeatException() {
    }
}
