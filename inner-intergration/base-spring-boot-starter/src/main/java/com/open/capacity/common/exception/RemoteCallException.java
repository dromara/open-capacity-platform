package com.open.capacity.common.exception;

/**
 * 服务调用异常
 * @author owen
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
public class RemoteCallException extends RuntimeException {
    private static final long serialVersionUID = 6610083281801529147L;

    public RemoteCallException(String message) {
        super(message);
    }
}
