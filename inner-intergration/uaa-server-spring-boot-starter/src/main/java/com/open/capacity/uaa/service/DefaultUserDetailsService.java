package com.open.capacity.uaa.service;

import org.springframework.plugin.core.Plugin;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.security.SocialUserDetails;

/**
 * 用户类型插件
 * @author owen
 * @date 2018/12/28
 * 插件模型
 */
public interface DefaultUserDetailsService extends UserDetailsService , Plugin<String> {
    /**
     * 根据电话号码查询用户
     *
     * @param mobile
     * @return
     */
    UserDetails loadUserByMobile(String mobile);

    /**
     * 根据用户id查询用户
     * @param userId 用户id/openId
     */
    SocialUserDetails loadUserByUserId(String userId) throws UsernameNotFoundException;
    
    
    /**
     * 根据用户openId查询用户
     * @param userId 用户id/openId
     */
    SocialUserDetails loadUserByOpenId(String openId) throws UsernameNotFoundException;
    
}
