package com.open.capacity.common.lock;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author someday
 * @date 2020/6/6
 *  code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Lock {
    /**
     * 锁的key
     */
    String key();
    /**
     * 获取锁的最大尝试时间(单位 {@code unit})
     * 该值大于0则使用 locker.tryLock 方法加锁，否则使用 locker.lock 方法
     */
    long waitTime() default 0;
    /**
     * 加锁的时间(单位 {@code unit})，超过这个时间后锁便自动解锁；
     * 如果leaseTime为-1，则保持锁定直到显式解锁
     */
    long leaseTime() default -1;
    /**
     * 参数的时间单位
     */
    TimeUnit unit() default TimeUnit.SECONDS;
    /**
     * 是否公平锁
     */
    boolean isFair() default false;
}
