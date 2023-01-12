package com.open.capacity.common.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.open.capacity.common.constant.ServiceNameConstants;
import com.open.capacity.common.feign.fallback.UserFeignClientFallbackFactory;
import com.open.capacity.common.model.LoginAppUser;
import com.open.capacity.common.model.SysUser;

/**
* @author 作者 owen 
* @version 创建时间：2017年11月12日 上午22:57:51
* 调用用户中心中的userdetail对象，用户oauth中的登录
* 获取的用户与页面输入的密码 进行BCryptPasswordEncoder匹配
 */
@FeignClient(name = ServiceNameConstants.USER_SERVICE,configuration = FeignExceptionConfig.class , fallbackFactory = UserFeignClientFallbackFactory.class, decode404 = true)
public interface UserFeignClient {
	
	
	 /**
     * 根据userId查询用户信息
     *
     * @param userId userId
     */
    @GetMapping(value = "/users-anon/userId", params = "userId")
    LoginAppUser findByUserId(@RequestParam("userId")  String userId);
	
    /**
     * feign rpc访问远程/users/{username}接口
     * 查询用户实体对象SysUser
     *
     * @param username
     * @return
     */
    @GetMapping(value = "/users/name/{username}")
    SysUser selectByUsername(@PathVariable("username") String username);

    /**
     * feign rpc访问远程/users-anon/login接口
     *
     * @param username
     * @return
     */
    @GetMapping(value = "/users-anon/login", params = "username")
    LoginAppUser findByUsername(@RequestParam("username") String username);

    /**
     * 通过手机号查询用户、角色信息
     *
     * @param mobile 手机号
     */
    @GetMapping(value = "/users-anon/mobile", params = "mobile")
    LoginAppUser findByMobile(@RequestParam("mobile") String mobile);

    /**
     * 根据OpenId查询用户信息
     *
     * @param openId openId
     */
    @GetMapping(value = "/users-anon/openId", params = "openId")
    LoginAppUser findByOpenId(@RequestParam("openId") String openId);
	
	
	
	
}
