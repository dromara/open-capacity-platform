package com.open.capacity.uaa.service.impl;

import javax.annotation.Resource;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.stereotype.Service;

import com.open.capacity.common.constant.SecurityConstants;
import com.open.capacity.common.feign.UserFeignClient;
import com.open.capacity.common.model.LoginAppUser;
import com.open.capacity.uaa.service.DefaultUserDetailsService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author someday
 * 默认后台用户类型插件
 */
@Slf4j
@Service
public class UserDetailServiceImpl implements DefaultUserDetailsService {
    private static final String ACCOUNT_TYPE = SecurityConstants.DEF_ACCOUNT_TYPE;

    @Resource
    private UserFeignClient userFeignClient;

    @Override
    public boolean supports(String accountType) {
        return ACCOUNT_TYPE.equals(accountType);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        LoginAppUser loginAppUser = userFeignClient.findByUsername(username);
        return  loginAppUser ;
    }

    @Override
	public SocialUserDetails loadUserByUserId(String userId) throws UsernameNotFoundException {
		LoginAppUser loginAppUser = userFeignClient.findByUserId(userId);
        return  loginAppUser ;
	}
    
    @Override
    public SocialUserDetails loadUserByOpenId(String openId) {
        LoginAppUser loginAppUser = userFeignClient.findByOpenId(openId);
        return  loginAppUser ;
    }

    @Override
    public UserDetails loadUserByMobile(String mobile) {
        LoginAppUser loginAppUser = userFeignClient.findByMobile(mobile);
        return  loginAppUser ;
    }


	
}
