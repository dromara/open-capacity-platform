package com.open.capacity.common.encryptbody.exception;

/**
 * <p>加密方式未找到或未定义异常</p>
 * @author licoy.cn
 * @version 2018/9/6
 * code:https://gitee.com/licoy/encrypt-body-spring-boot-starter
 */
public class EncryptMethodNotFoundException extends RuntimeException {

    public EncryptMethodNotFoundException() {
        super("Encryption method is not defined. (加密方式未定义)");
    }

    public EncryptMethodNotFoundException(String message) {
        super(message);
    }
}
