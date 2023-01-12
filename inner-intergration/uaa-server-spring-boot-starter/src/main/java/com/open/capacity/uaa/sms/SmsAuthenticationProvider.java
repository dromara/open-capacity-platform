package com.open.capacity.uaa.sms;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.open.capacity.uaa.common.token.SmsCodeAuthenticationToken;
import com.open.capacity.uaa.service.impl.UserDetailServiceFactory;
import com.open.capacity.uaa.utils.PreAuthenticationChecks;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SmsAuthenticationProvider implements  AuthenticationProvider {
    
    private UserDetailServiceFactory userDetailsServiceFactory;
    private PasswordEncoder passwordEncoder;
    private UserDetailsChecker detailsChecker = new PreAuthenticationChecks();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        SmsCodeAuthenticationToken authenticationToken = (SmsCodeAuthenticationToken) authentication;
        String mobile = (String) authenticationToken.getPrincipal();
        UserDetails user = userDetailsServiceFactory.getService(authenticationToken).loadUserByMobile(mobile);
        detailsChecker.check(user);
        SmsCodeAuthenticationToken authenticationResult = new SmsCodeAuthenticationToken(user, user.getAuthorities());
        // 需要把未认证中的一些信息copy到已认证的token中
        authenticationResult.setDetails(authenticationToken.getDetails());
        return authenticationResult;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return SmsCodeAuthenticationToken.class.isAssignableFrom(aClass);
    }

}
