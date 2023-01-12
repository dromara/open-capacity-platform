package com.open.capacity.common.feign.fallback;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.web.bind.annotation.RequestParam;

import com.open.capacity.common.feign.SmsFeignClient;

import lombok.extern.slf4j.Slf4j;

/**
 * UserFeignClient降级工场
 *
 * @author someday
 * @date 2018/1/18
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@Slf4j
public class SmsFeignClientFallbackFactory implements FallbackFactory<SmsFeignClient> {
    @Override
    public SmsFeignClient create(Throwable throwable) {
        return new SmsFeignClient() {
			@Override
			public String sendSmsCode(@RequestParam("phone") String phone ) {
				return "";
			}
        };
    }
}
