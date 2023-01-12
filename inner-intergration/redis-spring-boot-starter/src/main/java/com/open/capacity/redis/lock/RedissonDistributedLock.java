package com.open.capacity.redis.lock;


import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import com.open.capacity.common.constant.CommonConstant;
import com.open.capacity.common.exception.LockException;
import com.open.capacity.common.lock.DistributedLock;
import com.open.capacity.common.lock.LockAdapter;

/**
 * Description: redisson分布式锁, 支持全局与局部锁
 * @author zlt
 * @date 2020/5/5
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@ConditionalOnClass(RedissonClient.class)
@ConditionalOnProperty(prefix = "ocp.lock", name = "lockerType", havingValue = "redis", matchIfMissing = true)
public class RedissonDistributedLock implements DistributedLock {
    @Autowired
    private RedissonClient redisson;

    private LockAdapter getLock(String key, boolean isFair) {
        RLock lock;
        if (isFair) {
            lock = redisson.getFairLock(CommonConstant.LOCK_KEY_PREFIX + ":" + key);
        } else {
            lock =  redisson.getLock(CommonConstant.LOCK_KEY_PREFIX + ":" + key);
        }
        return new LockAdapter(lock, this);
    }

    @Override
    public LockAdapter lock(String key, long leaseTime, TimeUnit unit, boolean isFair) {
        LockAdapter LockAdapter = getLock(key, isFair);
        RLock lock = (RLock)LockAdapter.getLock();
        lock.lock(leaseTime, unit);
        return LockAdapter;
    }

    @Override
    public LockAdapter tryLock(String key, long waitTime, long leaseTime, TimeUnit unit, boolean isFair) throws InterruptedException {
        LockAdapter LockAdapter = getLock(key, isFair);
        RLock lock = (RLock)LockAdapter.getLock();
        if (lock.tryLock(waitTime, leaseTime, unit)) {
            return LockAdapter;
        }
        return null;
    }

    @Override
    public void unlock(Object lock) {
        if (lock != null) {
            if (lock instanceof RLock) {
                RLock rLock = (RLock)lock;
                if (rLock.isLocked()) {
                    rLock.unlock();
                }
            } else {
                throw new LockException("requires RLock type");
            }
        }
    }
}
