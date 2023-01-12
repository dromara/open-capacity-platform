/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.commom.utli.exception;

import com.open.capacity.ext.exception.HsExtRuntimeException;

/**
 * 2019/8/30 01:05 <br>
 * Description: ReflectException
 *
 * @author hillchen
 */
public class ReflectException extends HsExtRuntimeException {
    public ReflectException() {
    }

    public ReflectException(String message) {
        super(message);
    }

    public ReflectException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReflectException(Throwable cause) {
        super(cause);
    }

    public ReflectException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
