package com.open.capacity.uaa.mobile;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.open.capacity.uaa.common.token.MobileAuthenticationToken;
import com.open.capacity.uaa.service.impl.UserDetailServiceFactory;
import com.open.capacity.uaa.utils.PreAuthenticationChecks;

import cn.hutool.core.lang.Assert;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

/**
 * @author someday
 */
@Setter
@Getter
public class MobileAuthenticationProvider implements AuthenticationProvider {
    private UserDetailServiceFactory userDetailsServiceFactory;
    private PasswordEncoder passwordEncoder;
    private UserDetailsChecker detailsChecker = new PreAuthenticationChecks();

    @Override
    @SneakyThrows
    public Authentication authenticate(Authentication authentication) {
        MobileAuthenticationToken authenticationToken = (MobileAuthenticationToken) authentication;
        String mobile = (String) authenticationToken.getPrincipal();
        String password = (String) authenticationToken.getCredentials();
        UserDetails user = userDetailsServiceFactory.getService(authenticationToken).loadUserByMobile(mobile);
        detailsChecker.check(user);
        Assert.isTrue(passwordEncoder.matches(password, user.getPassword()), () -> {throw new BadCredentialsException("密码错误");});
        MobileAuthenticationToken authenticationResult = new MobileAuthenticationToken(user, password, user.getAuthorities());
        authenticationResult.setDetails(authenticationToken.getDetails());
        return authenticationResult;
    }
    /**
	 * providerManager会遍历所有 SecurityConfig中注册的provider集合
	 * 根据此方法返回true或false来决定由哪个provider 去校验请求过来的authentication
	 */
    @Override
    public boolean supports(Class<?> authentication) {
        return MobileAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
