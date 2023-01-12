/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.common.core.crypto.exception;

import com.open.capacity.common.core.exception.HltRuntimeException;

public class CryptoCheckSignErrorException extends HltRuntimeException {
    public CryptoCheckSignErrorException() {
    }

    public CryptoCheckSignErrorException(String message) {
        super(message);
    }

    public CryptoCheckSignErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public CryptoCheckSignErrorException(Throwable cause) {
        super(cause);
    }

    public CryptoCheckSignErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
