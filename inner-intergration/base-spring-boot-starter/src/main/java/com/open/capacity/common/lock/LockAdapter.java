package com.open.capacity.common.lock;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 锁对象抽象
 *
 * @author someday
 * @date 2020/7/28
 *  code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@AllArgsConstructor
public class LockAdapter implements AutoCloseable {
    @Getter
    private final Object lock;

    private final DistributedLock locker;

    @Override
    public void close() throws Exception {
        locker.unlock(lock);
    }
}
