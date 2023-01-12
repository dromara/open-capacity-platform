package com.open.capacity.common.feign;

import com.open.capacity.common.model.SysMenu;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.Future;

/**
 * blog: https://blog.51cto.com/13005375
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@Component
public class AsynMenuService {
    @Lazy
    @Resource
    private MenuFeignClient menuFeignClient;

    @Async
    public Future<List<SysMenu>> findByRoleCodes(String roleCodes) {
        List<SysMenu> result = menuFeignClient.findByRoleCodes(roleCodes);
        return new AsyncResult<>(result);
    }
}
