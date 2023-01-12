package com.open.capacity.uaa.service;

import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;


/**
 * @author owen
 * @date 2018/8/5
 * 根据ticket加载用户
 */
public interface ICasOauthUserDetailService extends  AuthenticationUserDetailsService<CasAssertionAuthenticationToken>{

}
