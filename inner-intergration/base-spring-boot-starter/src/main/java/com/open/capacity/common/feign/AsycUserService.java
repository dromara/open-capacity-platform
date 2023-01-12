package com.open.capacity.common.feign;

import java.util.concurrent.Future;

import javax.annotation.Resource;

import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import com.open.capacity.common.model.LoginAppUser;

/**
 * blog: https://blog.51cto.com/13005375
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@Component
public class AsycUserService {
    @Lazy
    @Resource
    private UserFeignClient userFeignClient;

    @Async
    public Future<LoginAppUser> findByUserName(String username) {
    	LoginAppUser result = userFeignClient.findByUsername(username);
        return new AsyncResult<>(result);
    }
}
