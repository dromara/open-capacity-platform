package com.open.capacity.common.encryptbody.exception;

/**
 * <p>非法的安全类型</p>
 *
 * @author licoy.cn
 * @version 2022/3/29
 * code:https://gitee.com/licoy/encrypt-body-spring-boot-starter
 */
public class IllegalSecurityTypeException extends RuntimeException {

    public IllegalSecurityTypeException() {
        super("illegal security type. (非法的安全类型)");
    }

    public IllegalSecurityTypeException(String message) {
        super(message);
    }
}