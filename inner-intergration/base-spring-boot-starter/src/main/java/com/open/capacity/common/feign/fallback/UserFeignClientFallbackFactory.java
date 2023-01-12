package com.open.capacity.common.feign.fallback;

import org.springframework.cloud.openfeign.FallbackFactory;

import com.open.capacity.common.feign.UserFeignClient;
import com.open.capacity.common.model.LoginAppUser;
import com.open.capacity.common.model.SysUser;

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
public class UserFeignClientFallbackFactory implements FallbackFactory<UserFeignClient> {
    @Override
    public UserFeignClient create(Throwable throwable) {
        return new UserFeignClient() {
            @Override
            public SysUser selectByUsername(String username) {
                log.error("通过用户名查询用户异常:{}", username, throwable);
                return new SysUser();
            }

            @Override
            public LoginAppUser findByUsername(String username) {
                log.error("通过用户名查询用户异常:{}", username, throwable);
                return new LoginAppUser();
            }

            @Override
            public LoginAppUser findByMobile(String mobile) {
                log.error("通过手机号查询用户异常:{}", mobile, throwable);
                return new LoginAppUser();
            }

            @Override
            public LoginAppUser findByUserId(String userId) {
                log.error("通过userId查询用户异常:{}", userId, throwable);
                return new LoginAppUser();
            }
            
            @Override
            public LoginAppUser findByOpenId(String openId) {
                log.error("通过openId查询用户异常:{}", openId, throwable);
                return new LoginAppUser();
            }
            
            
        };
    }
}
